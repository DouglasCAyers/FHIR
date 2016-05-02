package com.example;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.example.mixin.Mixin_HealthCloudGA__EhrPatient__c;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sforce.soap.enterprise.sobject.HealthCloudGA__EhrPatient__c;

@Configuration
public class FhirWebMvcConfiguration extends WebMvcConfigurationSupport {

	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {

		// http://stackoverflow.com/questions/16332092/spring-mvc-pathvariable-with-dot-is-getting-truncated
		// https://www.javacodegeeks.com/2013/01/spring-mvc-customizing-requestmappinghandlermapping.html

		RequestMappingHandlerMapping bean = super.requestMappingHandlerMapping();

		bean.setUseSuffixPatternMatch( false );

		return bean;
	}

	@Override
	protected void extendMessageConverters( List<HttpMessageConverter<?>> converters ) {

		for ( HttpMessageConverter<?> converter : converters ) {

			if ( converter instanceof MappingJackson2HttpMessageConverter ) {

				MappingJackson2HttpMessageConverter jacksonConverter = (MappingJackson2HttpMessageConverter) converter;

				jacksonConverter.setPrettyPrint( true );
				jacksonConverter.getObjectMapper().setSerializationInclusion( Include.NON_NULL );
				jacksonConverter.getObjectMapper().setDateFormat( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" ) );
				jacksonConverter.getObjectMapper().setTimeZone(  TimeZone.getTimeZone( "GMT-00:00" ) );

				jacksonConverter.getObjectMapper().addMixIn( HealthCloudGA__EhrPatient__c.class, Mixin_HealthCloudGA__EhrPatient__c.class );

			}

		}

	}

}
