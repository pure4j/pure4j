package org.pure4j.examples.var_model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Currency;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.IPersistentVector;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.collections.PersistentList;
import org.pure4j.collections.PersistentVector;
import org.pure4j.examples.lambda.var_model.pure.Amount;
import org.pure4j.examples.lambda.var_model.pure.PnLStream;
import org.pure4j.examples.lambda.var_model.pure.Sensitivity;
import org.pure4j.examples.lambda.var_model.pure.Ticker;
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

	IPersistentMap<Ticker, PnLStream> historic;

	public TestVarModel() throws IOException {
		super();
		this.historic = loadHistoricData();
	}

	@Test
	public void testSingleSensitivity() throws IOException {
		VarProcessor processor = new VarProcessorImpl(.99f, Currency.getInstance("GBP"));
		PersistentList<Sensitivity> sensitivities = new PersistentList<Sensitivity>();
		PersistentHashMap<Currency, Float> fx = new PersistentHashMap<Currency, Float>();
		sensitivities = sensitivities.cons(new Sensitivity(new Ticker("LIBOR1M"), 3.1f));
		sensitivities = sensitivities.cons(new Sensitivity(new Ticker("EURGBP"), 104.0f));
		Amount done = processor.getVar(historic, sensitivities, fx);
		Assert.assertEquals(new Amount(Currency.getInstance("GBP"), 22.57f), done);
	}

	/**
	 * Loading data in from a file - not pure.
	 */
	private IPersistentMap<Ticker, PnLStream> loadHistoricData() throws IOException {
		PersistentHashMap<Ticker, PnLStream> out= new PersistentHashMap<Ticker, PnLStream>();
		InputStream is = this.getClass().getResourceAsStream("/historic_var.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		
		// first row is the header
		IPersistentVector<LocalDate> dates = PersistentVector.emptyVector();
		String[] headerRow = line.split(",");
		for (int i = 2; i < headerRow.length; i++) {
			LocalDate ta = LocalDate.parse(headerRow[i].trim());
			dates = dates.cons(ta);
		}

		line = br.readLine();

		while (line != null) {
			String[] parts = line.split(",");
			String ticker = parts[0];
			String ccy = parts[1];
			IPersistentMap<LocalDate, Float> pnls = new PersistentHashMap<LocalDate, Float>();
			for (int i = 2; i < parts.length; i++) {
				Float f = Float.parseFloat(parts[i]);
				pnls = pnls.assoc(dates.get(i-2), f);
			}
			
			PnLStream pnl = new PnLStream(pnls, Currency.getInstance(ccy.trim()));
			out = out.assoc(new Ticker(ticker), pnl);
			line = br.readLine();

		}
		
		return out;
	}
}
