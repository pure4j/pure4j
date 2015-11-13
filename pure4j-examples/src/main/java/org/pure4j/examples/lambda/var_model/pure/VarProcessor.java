package org.pure4j.examples.lambda.var_model.pure;

import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.ISeq;

public interface VarProcessor {

	float getVar(IPersistentMap<String, PnLStream> historic, ISeq<Sensitivity> sensitivities);
}
