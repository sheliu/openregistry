package org.openregistry.core.web.resources;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.openregistry.core.domain.sor.PersonSearch;
import org.openregistry.core.domain.jpa.sor.JpaSorPersonSearchImpl;
import org.openregistry.core.domain.Name;
import org.openregistry.core.domain.Person;
import org.openregistry.core.domain.Identifier;
import org.openregistry.core.service.PersonService;
import org.openregistry.core.service.ServiceExecutionResult;
import org.openregistry.core.service.reconciliation.ReconciliationResult;
import org.openregistry.core.service.reconciliation.PersonMatch;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.annotation.XmlElement;
import javax.annotation.Resource;
import java.net.URI;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Root RESTful resource representing people in Open Registry.
 * This component is managed and autowired by Spring by means of context-component-scan,
 * and served by Jersey when URI is matched against the @Path definition. This bean is a singleton,
 * and therefore is thread-safe.
 *
 * @author Dmitriy Kopylenko
 * @since 1.0
 */
@Path("/people")
@Component
@Scope("singleton")
public class PeopleResource {

    //Jersey specific injection
    @Context
    UriInfo uriInfo;

    @Autowired
    private PersonService personService;

    //JSR-250 injection which is more appropriate here for 'autowiring by name' in the case of multiple types
    //are defined in the app ctx (Strings). The looked up bean name defaults to the property name which
    //needs an injection.
    @Resource
    private String preferredPersonIdentifierType;


    @GET
    @Path("{personIdType}/{personId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    //auto content negotiation!
    public PersonResponseRepresentation showPerson(@PathParam("personId") String personId,
                                                   @PathParam("personIdType") String personIdType) {

        //Build activation generator URI
        URI activationGeneratorUri = this.uriInfo.getAbsolutePathBuilder().path("activation").build();

        //Build activation proccess URI
        URI activationProcessorUri = this.uriInfo.getAbsolutePathBuilder().path("activation").path("proccess")
                .queryParam("activation-token", "activation-token-skjfskjfhskjdfh").build();

        return new PersonResponseRepresentation(
                activationGeneratorUri.toString(),
                activationProcessorUri.toString(),
                "AT-153254325",
                Arrays.asList(new PersonResponseRepresentation.PersonIdentifierRepresentation(personIdType, personId),
                        new PersonResponseRepresentation.PersonIdentifierRepresentation("rcpId", "12345")));
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    //resp-type query param is here temporarily, for testing
    public Response processIncomingPerson(@DefaultValue("201") @QueryParam("resp-type") int respType,
                                          MultivaluedMap<String, String> formParams) {

        Response response = null;
        URI uri = null;
        PersonSearch personSearch = null;
        try {
            personSearch = personRequestToPersonSearch(new PersonRequestRepresentation(formParams));
        }
        catch (IllegalArgumentException ex) {
            //HTTP 400
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }

        ServiceExecutionResult result = this.personService.addPerson(personSearch, null);
        //Now do the branching logic based on the result
        if (result.succeeded()) {
            if (personCreated(result.getReconciliationResult().getReconciliationType())) {
                uri = buildPersonResourceUri((Person) result.getTargetObject());
                response = Response.created(uri).build();
            }
            else if (personAlreadyExists(result.getReconciliationResult().getReconciliationType())) {
                uri = buildPersonResourceUri((Person) result.getTargetObject());
                response = Response.temporaryRedirect(uri).build();
            }
        }
        else {
            if (result.getValidationErrors().size() > 0) {
                //TODO: add more informative message to the entity body
                return Response.status(Response.Status.BAD_REQUEST).entity("The incoming request is malformed.").build();
            }
            else if (multiplePeopleFound(result.getReconciliationResult().getReconciliationType())) {
                List<PersonMatch> conflictingPeopleFound = result.getReconciliationResult().getMatches();
                response = Response.status(409).entity(buildLinksToConflictingPeopleFound(conflictingPeopleFound))
                        .type(MediaType.APPLICATION_XHTML_XML).build();
            }
        }

        /*switch (respType) {
            case 201:
                response = Response.created(uri).build();
                break;
            case 409:
                LinkRepresentation entityBody =
                        new LinkRepresentation(Arrays.asList(new LinkRepresentation.Link("person", uri.toString()),
                                new LinkRepresentation.Link("person", uri.toString())));

                response = Response.status(409).entity(entityBody).type(MediaType.APPLICATION_XHTML_XML).build();
                break;
            case 307:
                response = Response.temporaryRedirect(uri).build();
            default:
                break;
        }*/
        System.out.println("GOT HTTP form POSTed: " + formParams);
        return response;
    }

    private PersonSearch personRequestToPersonSearch(PersonRequestRepresentation request) {
        JpaSorPersonSearchImpl ps = new JpaSorPersonSearchImpl();
        ps.getPerson().setSourceSorIdentifier(String.valueOf(request.getSystemOfRecordId()));
        ps.getPerson().setSorId(String.valueOf(request.getSystemOfRecordPersonId()));
        Name name = ps.getPerson().addName();
        name.setGiven(request.getFirstName());
        name.setFamily(request.getLastName());
        ps.setEmailAddress(request.getEmail());
        ps.setPhoneNumber(request.getPhoneNumber());
        try {
            ps.getPerson().setDateOfBirth(new SimpleDateFormat("mmddyyyy").parse(request.getDateOfBirth()));
        }
        catch (Exception ex) {
            //Let the validation in JpaSorPersonSearchImpl catch the null property. Carry on...
        }
        ps.getPerson().setSsn(request.getSsn());
        ps.getPerson().setGender(request.getGender());
        ps.setAddressLine1(request.getAddressLine1());
        ps.setAddressLine2(request.getAddressLine2());
        ps.setCity(request.getCity());
        ps.setRegion(request.getRegion());
        ps.setPostalCode(request.getPostalCode());

        return ps;
    }

    private URI buildPersonResourceUri(Person person) {
        for (Identifier id : person.getIdentifiers()) {
            if (this.preferredPersonIdentifierType.equals(id.getType().getName())) {
                return this.uriInfo.getAbsolutePathBuilder().path(this.preferredPersonIdentifierType)
                        .path(id.getValue()).build();
            }
        }
        //Person MUST have at least one id of the preferred configured type. Results in HTTP 500
        throw new IllegalStateException("The person must have at least one id of the preferred configured type " +
                "which is <" + this.preferredPersonIdentifierType + ">");
    }

    private LinkRepresentation buildLinksToConflictingPeopleFound(List<PersonMatch> matches) {
        //A little defensive stuff. Will result in HTTP 500
        if (matches.size() == 0) {
            throw new IllegalStateException("Person matches cannot be empty if reconciliation result is <MAYBE>");
        }
        List<LinkRepresentation.Link> links = new ArrayList<LinkRepresentation.Link>();
        for (PersonMatch match : matches) {
            links.add(new LinkRepresentation.Link("person", buildPersonResourceUri(match.getPerson()).toString()));
        }
        return new LinkRepresentation(links);
    }

    //TODO: possibly refactor these methods into a helper type or encapsulate them in ReconciliationResult itself? 
    private boolean personCreated(ReconciliationResult.ReconciliationType reconciliationType) {
        return (reconciliationType == ReconciliationResult.ReconciliationType.NONE);
    }

    private boolean personAlreadyExists(ReconciliationResult.ReconciliationType reconciliationType) {
        return (reconciliationType == ReconciliationResult.ReconciliationType.EXACT);
    }

    private boolean multiplePeopleFound(ReconciliationResult.ReconciliationType reconciliationType) {
        return (reconciliationType == ReconciliationResult.ReconciliationType.MAYBE);
    }
}
