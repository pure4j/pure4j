package org.pure4j.examples.var_model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;

import org.concordion.api.extension.Extensions;
import org.concordion.ext.excel.ExcelExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.pure4j.collections.IPersistentList;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.collections.PersistentList;
import org.pure4j.examples.lambda.var_model.pure.PnLStream;
import org.pure4j.examples.lambda.var_model.pure.Sensitivity;
import org.pure4j.examples.lambda.var_model.pure.VarProcessor;
import org.pure4j.examples.lambda.var_model.pure.VarProcessorImpl;

@RunWith(ConcordionRunner.class)
@Extensions(ExcelExtension.class)
public class ConcordionVarTest {
	
	IPersistentMap<String, PnLStream> theStreams = new PersistentHashMap<String, PnLStream>();
	IPersistentList<Sensitivity> sensitivities = new PersistentList<Sensitivity>();

	public void addPnLRow(String date, String goog, String yhoo, String msft) {
		theStreams = addPnlPoint(theStreams, "GOOG", date, goog);
		theStreams = addPnlPoint(theStreams, "YHOO", date, yhoo);
		theStreams = addPnlPoint(theStreams, "MSFT", date, msft);
	}

	private IPersistentMap<String, PnLStream> addPnlPoint(IPersistentMap<String, PnLStream> theStreams, String sensitivity, String date, String value) {
		PnLStream stream = theStreams.get(sensitivity);
		IPersistentMap<LocalDate, Float> pnls;
		if (stream == null) {
			pnls = new PersistentHashMap<LocalDate, Float>();
		} else {
			pnls = stream.getPnls();
		}
		
		LocalDate localDate = LocalDate.parse(date);
		Float f = parsePercentage(value);
		pnls = pnls.assoc(localDate, f);
		
		return theStreams.assoc(sensitivity, new PnLStream(pnls));
	}
	
	public void setSensitivity(String ticker, String amount) {
		Float f = Float.parseFloat(amount.substring(amount.lastIndexOf("]")+1));
		sensitivities = sensitivities.cons(new Sensitivity(ticker, f));
	}
	
	public String calculateVaR(String percentage) {
		float conf = parsePercentage(percentage);
		VarProcessor processor = new VarProcessorImpl(conf);
		float result = processor.getVar(theStreams, sensitivities.seq());
		return new DecimalFormat("#.000").format(result);
	}
	
	private float parsePercentage(String percentage) {
		return Float.parseFloat(percentage.trim().substring(0, percentage.trim().length()-1)) / 100f;
	}
}
