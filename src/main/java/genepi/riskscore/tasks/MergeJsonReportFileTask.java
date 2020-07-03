package genepi.riskscore.tasks;

import genepi.riskscore.io.JsonReportFile;

public class MergeJsonReportFileTask {

	private String output;

	private String[] inputs;

	public void setOutput(String output) {
		this.output = output;
	}

	public void setInputs(String... inputs) {
		this.inputs = inputs;
	}

	public void run() throws Exception {
		assert (inputs != null);
		assert (output != null);
		assert (inputs.length > 0);

		JsonReportFile first = new JsonReportFile();
		first.load(inputs[0]);

		for (int i = 1; i < inputs.length; i++) {
			JsonReportFile next = new JsonReportFile();
			next.load(inputs[i]);
			first.merge(next);
		}

		first.save(output);
	}

}
