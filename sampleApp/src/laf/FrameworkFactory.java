package laf;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import laf.urlMapping.UrlMapping;

@Singleton
public class FrameworkFactory {
	@Produces UrlMapping produceUrlMapping(){
		UrlMapping result=new UrlMapping();
		return result;
	}
}
