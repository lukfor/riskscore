package genepi.riskscore.tasks;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import genepi.io.FileUtil;
import genepi.riskscore.commands.ApplyScoreCommand;
import genepi.riskscore.io.OutputFile;
import picocli.CommandLine;

public class MergeScoreTaskTest {

	@Test
	public void testMerge() throws Exception {

		MergeScoreTask task = new MergeScoreTask();
		task.setInputs("test-data/scores.chunk1.txt", "test-data/scores.chunk2.txt");
		task.setOutput("merged.task.txt");
		task.run();

		assertEquals(FileUtil.readFileAsString("test-data/merged.expected.txt"),
				FileUtil.readFileAsString("merged.task.txt"));
	}

	@Test
	public void testMergingChunks() throws Exception {

		// whoole file
		String[] args = { "test-data/chr20.dose.vcf.gz", "--ref", "PGS000018,PGS000027", "--out", "output.csv" };
		int result = new CommandLine(new ApplyScoreCommand()).execute(args);
		assertEquals(0, result);

		// chunks

		int lengthChr20 = 64444167;
		int chunkSize = 10000000;
		int chunks = (lengthChr20 / chunkSize);
		if (lengthChr20 % chunkSize > 0) {
			chunks++;
		}

		String[] chunkFiles = new String[chunks];

		int count = 0;
		for (int i = 1; i <= lengthChr20; i += chunkSize) {
			int start = i;
			int end = i + chunkSize - 1;
			String chunk = "output" + start + "_" + end + ".csv";
			args = new String[] { "test-data/chr20.dose.vcf.gz", "--ref", "PGS000018,PGS000027", "--start", start + "",
					"--end", end + "", "--out", chunk };
			result = new CommandLine(new ApplyScoreCommand()).execute(args);
			assertEquals(0, result);

			chunkFiles[count] = chunk;
			count++;
		}

		MergeScoreTask task = new MergeScoreTask();
		task.setInputs(chunkFiles);
		task.setOutput("output.merged.txt");
		task.run();

		assertEqualsScoreFiles("output.csv", "output.merged.txt", 0.0000001);

	}

	public void assertEqualsScoreFiles(String filename1, String filename2, double delta) throws IOException {

		OutputFile file1 = new OutputFile();
		file1.load(filename1);

		OutputFile file2 = new OutputFile();
		file2.load(filename2);

		assertEquals(file1.getSamples().size(), file2.getSamples().size());
		assertEquals(file1.getScores().size(), file2.getScores().size());
		for (int i = 0; i < file1.getScores().size(); i++) {
			assertEquals(file1.getScores().get(i), file2.getScores().get(i));
			List<Double> values1 = file1.getData()[i];
			List<Double> values2 = file2.getData()[i];
			assertEquals(values1.size(), values2.size());
			assertEquals(values1.size(), file1.getSamples().size());
			for (int j = 0; j < values1.size(); j++) {
				assertEquals(values1.get(j), values2.get(j), delta);
			}
		}
	}

}
