package com.bluersw.model

import net.sf.json.JSONObject

interface PipelineUtility {

	boolean isUnix()

	def sh(String script, boolean returnStatus, boolean returnStdout)

	def bat(String script, boolean returnStatus, boolean returnStdout)

	void junit(String testResults)

	void jacoco(boolean changeBuildStatus, String classPattern, String maximumLineCoverage, String maximumInstructionCoverage, String maximumMethodCoverage, String maximumBranchCoverage, String maximumClassCoverage, String maximumComplexityCoverage, String execPattern, String inclusionPattern, String exclusionPattern)

	Map readProperties(String file)

	JSONObject readJSON(String file)

	void println(String info)
}