package com.example.mixin;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sforce.soap.enterprise.sobject.HealthCloudGA__EhrPatient__c;

/**
 * http://wiki.fasterxml.com/JacksonMixInAnnotations
 */
public abstract class Mixin_HealthCloudGA__EhrPatient__c extends HealthCloudGA__EhrPatient__c {

	@JsonIgnore
	@Override
	public String[] getFieldsToNull() {
		return super.getFieldsToNull();
	}

	@JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" )
	@Override
	public Calendar getHealthCloudGA__BirthDate__c() {
		return super.getHealthCloudGA__BirthDate__c();
	}

}
