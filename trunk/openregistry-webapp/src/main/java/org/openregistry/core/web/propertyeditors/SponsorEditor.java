package org.openregistry.core.web.propertyeditors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.openregistry.core.repository.ReferenceRepository;
import org.openregistry.core.domain.Country;
import org.openregistry.core.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Iterator;
import java.beans.PropertyEditorSupport;

/**
 * Created by IntelliJ IDEA.
 * User: Nancy Mond
 * Date: Feb 5, 2009
 * Time: 9:39:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class SponsorEditor extends PropertyEditorSupport {
    private String format;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    ReferenceRepository referenceRepository;

    public SponsorEditor(ReferenceRepository referenceRepository){
        this.referenceRepository = referenceRepository;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAsText(){
        Person person = (Person) getValue();
        return person != null ? String.valueOf(person.getId()) : " ";
    }

    @Override 
    public void setAsText(String text) {
        if (StringUtils.hasText(text)){
            setValue(referenceRepository.getPersonById(new Long(text)));
        } else{
            setValue(null);
        }
    }
}
