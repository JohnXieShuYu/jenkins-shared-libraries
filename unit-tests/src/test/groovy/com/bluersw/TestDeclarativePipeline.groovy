package com.bluersw

import com.lesfurets.jenkins.unit.declarative.DeclarativePipelineTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.LocalSource.localSource

class TestDeclarativePipeline extends DeclarativePipelineTest {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder()

	String sharedLibs = this.class.getResource('/libs').getFile()

	@Override
	@Before
	void setUp() throws Exception {
		scriptRoots += 'src/main/jenkins'
		super.setUp()
		binding.setVariable('scm', [branch: 'master'])}

	@Test
	void test_BuildJavaDeclarative() throws Exception {
		boolean exception = false
		def library = library().name('shared-library')
							   .defaultVersion("master")
							   .allowOverride(false)
							   .implicit(false)
							   .targetPath(sharedLibs)
							   .retriever(localSource(sharedLibs))
							   .build()
		helper.registerSharedLibrary(library)
		runScript('com/bluersw/testBuildJavaDeclarative.Jenkinsfile')
		assertJobStatusSuccess()
	}
}
