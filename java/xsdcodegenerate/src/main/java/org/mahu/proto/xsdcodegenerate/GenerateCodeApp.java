package org.mahu.proto.xsdcodegenerate;

import org.mahu.proto.xsdcodegenerate.xml.AppReport;
import org.mahu.proto.xsdcodegenerate.xml.AppSettings;

public class GenerateCodeApp extends MyBase {

	public static void main(String[] args) {
		GenerateCodeApp gc = new GenerateCodeApp();
		gc.executeSteps();
	}

	private void executeSteps() {
		executeStepsAppSettings();
		//executeStepsReport();
	}
	
	private void executeStepsAppSettings() {
		final String structName = "settings";
		final String jObjectRef = "jSettingsRef";
		generateCodeForReadingXml(structName,jObjectRef,AppSettings.class);
	}
	
	private void executeStepsReport() {
		final String structName = "report";
		final String jObjectRef = "jReportRef";
		generateCodeForReadingXml(structName,jObjectRef,AppReport.class);
	}	

	private void generateCodeForReadingXml(final String structName, final String jObjectRef, final Class<?> rootClass) {

		MyClassMngr mgr = new MyClassMngr();

		ClassUtils util = new ClassUtils(mgr);
		MyClass myRootClass = util.inspectClass(rootClass);

		println("##############################################################");

		XmlReadViaJniCodeGenerator gen1 = new XmlReadViaJniCodeGenerator(mgr);
		gen1.generateCodeForReadingOfXmlObjects(structName,jObjectRef, myRootClass);
	}
	
	private void executeStepsForWritingXml(final Class<?> rootClass) {

		MyClassMngr mgr = new MyClassMngr();

		ClassUtils util = new ClassUtils(mgr);
		MyClass myRootClass = util.inspectClass(rootClass);

		println("##############################################################");

	}	

}