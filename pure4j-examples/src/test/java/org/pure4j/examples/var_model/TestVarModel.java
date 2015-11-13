package org.pure4j.examples.var_model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.IPersistentVector;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.collections.PersistentList;
import org.pure4j.collections.PersistentVector;
import org.pure4j.examples.lambda.var_model.pure.PnLStream;
import org.pure4j.examples.lambda.var_model.pure.Sensitivity;
import org.pure4j.examples.lambda.var_model.pure.VarProcessor;
import org.pure4j.examples.lambda.var_model.pure.VarProcessorImpl;

/**
 * I wonder if this would be a good spot for an excel concordion test?
 * Let's go with something simple for now!
 * 
 * @author robmoffat
 *
 */
public class TestVarModel {

	IPersistentMap<String, PnLStream> historic;

	public TestVarModel() throws IOException {
		super();
		this.historic = loadHistoricData();
	}

	@Test
	public void testSingleSensitivity() throws IOException {
		VarProcessor processor = new VarProcessorImpl(.99f);
		PersistentList<Sensitivity> sensitivities = new PersistentList<Sensitivity>();
		sensitivities = sensitivities.cons(new Sensitivity("LIBOR1M", 3.1f));
		sensitivities = sensitivities.cons(new Sensitivity("EURGBP", 104.0f));
		float done = processor.getVar(historic, sensitivities);
		Assert.assertEquals(22.57f, done);
	}

	/**
	 * Loading data in from a file - not pure.
	 */
	private IPersistentMap<String, PnLStream> loadHistoricData() throws IOException {
		PersistentHashMap<String, PnLStream> out= new PersistentHashMap<String, PnLStream>();
		InputStream is = this.getClass().getResourceAsStream("/historic_var.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		
		// first row is the header
		IPersistentVector<LocalDate> dates = PersistentVector.emptyVector();
		String[] headerRow = line.split(",");
		for (int i = 1; i < headerRow.length; i++) {
			LocalDate ta = LocalDate.parse(headerRow[i].trim());
			dates = dates.cons(ta);
		}

		line = br.readLine();

		while (line != null) {
			String[] parts = line.split(",");
			String ticker = parts[0];
			IPersistentMap<LocalDate, Float> pnls = new PersistentHashMap<LocalDate, Float>();
			for (int i = 1; i < parts.length; i++) {
				Float f = Float.parseFloat(parts[i]);
				pnls = pnls.assoc(dates.get(i-1), f);
			}
			
			PnLStream pnl = new PnLStream(pnls);
			out = out.assoc(ticker, pnl);
			line = br.readLine();

		}
		
		return out;
	}
}
