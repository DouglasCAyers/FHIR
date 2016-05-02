package com.example;

import java.util.Calendar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sforce.soap.enterprise.sobject.HealthCloudGA__EhrPatient__c;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.server.EncodingEnum;

@Controller
@RequestMapping( "/fhir" )
public class FhirController {

	@RequestMapping(
		value = "/patient/{patientId}",
		method = RequestMethod.GET,
		produces = "application/json"
	)
	@ResponseBody
	public HealthCloudGA__EhrPatient__c getPatient( @PathVariable("patientId") String patientId ) {

		// http://jamesagnew.github.io/hapi-fhir/doc_intro.html#A_Note_on_FHIR_Versions

		// We're connecting to a DSTU1 compliant server in this example
		FhirContext ctx = FhirContext.forDstu2();
		// String serverBase = "http://fhirtest.uhn.ca/baseDstu2";
		String serverBase = "https://open-ic.epic.com/FHIR/api/FHIR/DSTU2/";

		IGenericClient client = ctx.newRestfulGenericClient( serverBase );
		client.setLogRequestAndResponse( true ); // turn off in production
		client.setPrettyPrint( true ); // turn off in production
		client.setEncoding( EncodingEnum.JSON ); // use JSON because EPIC's XML of FHIR is not legit, won't parse per actual HL7.org spec

		/*
		 *  Search Example
		 */

		Bundle results = client.search().forResource( Patient.class )
				.where( Patient.GIVEN.matches().value( "Jason" ) )
				.where( Patient.FAMILY.matches().value( "Argonaut" ) )
				.returnBundle( ca.uhn.fhir.model.dstu2.resource.Bundle.class )
				.execute();

		System.out.println( "Found " + results.getTotal() + " for name 'Jason Argonaut'" );

		/*
		 * Get Single Record Example
		 */

		Patient patient = client.read( Patient.class, patientId );

		System.out.println( patient );

		// Denormalize the FHIR response into Salesforce's Health Cloud object model
		// On the Salesforce side, can easily deserialize the JSON response into an sobject.
		/*
			String ENDPOINT_URL = 'http://localhost:8080/';

			HttpRequest req = new HttpRequest();
			req.setEndpoint(
				ENDPOINT_URL + 'patient' +
				'?patientId=' + EncodingUtil.urlEncode( patientId, 'UTF-8' )
			);
			req.setMethod('GET');

			Http http = new Http();
			HttpResponse res = http.send(req);

			System.debug( res + ': ' + res.getBody() );

			HealthCloudGA__EhrPatient__c patient = (HealthCloudGA__EhrPatient__c) JSON.deserialize(
				res.getBody(),
				HealthCloudGA__EhrPatient__c.class
			);

			System.debug( patient );
		*/

		HealthCloudGA__EhrPatient__c ehrPatient = new HealthCloudGA__EhrPatient__c();

		ehrPatient.setHealthCloudGA__SourceSystemId__c( patient.getId().getValueAsString() );

		Calendar birthDate = Calendar.getInstance();
		birthDate.setTime( patient.getBirthDate() );
		ehrPatient.setHealthCloudGA__BirthDate__c( birthDate );

		for ( int i = 1; i <= patient.getName().size(); i++ ) {

			HumanNameDt name = patient.getName().get( i - 1 );

			if ( i == 1 ) {
				ehrPatient.setHealthCloudGA__GivenName1__c( name.getGivenAsSingleString() );
				ehrPatient.setHealthCloudGA__FamilyName1__c( name.getFamilyAsSingleString() );
			} else if ( i == 2 ) {
				ehrPatient.setHealthCloudGA__GivenName2__c( name.getGivenAsSingleString() );
				ehrPatient.setHealthCloudGA__FamilyName2__c( name.getFamilyAsSingleString() );
			} else if ( i == 3 ) {
				ehrPatient.setHealthCloudGA__GivenName3__c( name.getGivenAsSingleString() );
				ehrPatient.setHealthCloudGA__FamilyName3__c( name.getFamilyAsSingleString() );
			}

		}

		return ehrPatient;
	}



}
