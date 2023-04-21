package org.colomoto.mddlib.internal;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.NodeRelation;
import org.colomoto.mddlib.VariableEffect;
import org.colomoto.mddlib.operators.MDDBaseOperators;

/**
 * MDDManager adding a custom order on top of an existing MDDStore.
 * It acts mostly as proxy to the actual store but provides a uniform API.
 * It should be created through the <code>getProxy</code> method.
 * For convenience, MDDManager offers a wrapper method: <code>getManager()</code>.
 * 
 * @author Aurelien Naldi
 */
public class MDDManagerProxy implements MDDManager {
	
	private final MDDStore store;
	private int[] store2custom, custom2store;
	private MDDVariable[] variables;
	
	
	public static MDDManager getProxy(MDDStore store, List<?> customOrder) {
		MDDVariable[] rawVariables = store.getAllVariables();
		
		// build order mapping
		boolean sameOrder = rawVariables.length == customOrder.size();
		int[] custom2store = new int[customOrder.size()];
		for (int i=0 ; i<custom2store.length ; i++) {
			custom2store[i] = -1;
		}
		int i = 0;
		for (Object v: customOrder) {
			MDDVariable var = store.getVariableForKey(v);
			if (var.order != i) {
				sameOrder = false;
			}
			custom2store[i] = var.order;
			i++;
		}
		
		if (sameOrder) {
			// no order mapping is needed
			return store;
		}
		
		// save the order mapping and compute the reverse one
		int[] store2custom = new int[rawVariables.length];
		for (i=0 ; i<store2custom.length ; i++) {
			store2custom[i] = -1;
		}
		i=0;
		for (int k: custom2store) {
			if (k >= 0) {
				store2custom[k] = i;
			}
			i++;
		}
		return new MDDManagerProxy(store, custom2store, store2custom);
	}
	
	private MDDManagerProxy(MDDStore store, int[] custom2store, int[] store2custom) {
		this.store = store;
		this.custom2store = custom2store;
		this.store2custom = store2custom;
		this.variables = new MDDVariable[custom2store.length];
		
		MDDVariable[] storeVars = store.getAllVariables();
		int i=0;
		for (int j: custom2store) {
			variables[i] = storeVars[j];
			i++;
		}
	}
	
	@Override
	public byte reach(int node, byte[] values) {
		return store.reach(node, values, store2custom);
	}

	@Override
	public byte groupReach(int node, byte[] values) {
		return store.groupReach(node, values, store2custom);
	}

	@Override
	public MDDVariable getVariableForKey(Object key) {
		MDDVariable var = store.getVariableForKey(key);
		int idx = store2custom[var.order];
		if (idx < 0) {
			// this variable is not is the custom order
			return null;
		}
		return var;
	}


    @Override
    public int nodeFromState(byte[] state, int value) {
        return store.nodeFromState(state, value, store2custom);
    }

    @Override
    public int nodeFromStates(Collection<byte[]> states, int value) {
        int node = 0;
        for (byte[] state: states) {
            int newNode = nodeFromState(state, value);
            int nextNode = MDDBaseOperators.OR.combine(this, node, newNode);
            free(newNode);
            free(node);
            node = nextNode;
        }
        return node;
    }

    @Override
	public int getVariableIndex(MDDVariable var) {
		return store2custom[var.order];
	}

	@Override
	public MDDVariable[] getAllVariables() {
		return variables;
	}


	@Override
	public MDDVariable ensureVariable(Object key, byte nbval) {
		MDDVariable inStore = store.ensureVariable(key, nbval);
		if (inStore.order >= variables.length) {
			MDDVariable[] extended = new MDDVariable[variables.length+1];
			System.arraycopy(variables, 0, extended, 0, variables.length);
			extended[variables.length] = inStore;
			this.variables = extended;
			
			custom2store = extendmapping(custom2store, variables.length);
			store2custom = extendmapping(store2custom, variables.length);
		}
		return inStore;
	}
	
	private int[] extendmapping(int[] mapping, int l) {
		if (l <= mapping.length) {
			return mapping;
		}
		int[] extended = new int[l];
		System.arraycopy(mapping, 0, extended, 0, mapping.length);
		for (int i=mapping.length ; i<extended.length ; i++) {
			extended[i] = i;
		}
		return extended;
	}
	
	/* **************** Pure proxy ************************* */
	
	@Override
	public MDDManager getManager(List<?> order) {
		return store.getManager(order);
	}

	@Override
	public MDDVariable getNodeVariable(int n) {
		return store.getNodeVariable(n);
	}

	@Override
	public void free(int pos) {
		store.free(pos);
	}

	@Override
	public int use(int node) {
		return store.use(node);
	}

	@Override
	public boolean isleaf(int node) {
		return store.isleaf(node);
	}

	@Override
	public int getChild(int node, int value) {
		return store.getChild(node, value);
	}

	@Override
	public int[] getChildren(int node) {
		return store.getChildren(node);
	}

	@Override
	public int not(int node) {
		return store.not(node);
	}

	@Override
	public int mnot(int node, int v) {
		return store.mnot(node, v);
	}

	@Override
	public NodeRelation getRelation(int first, int other) {
		return store.getRelation(first, other);
	}

	@Override
	public int getNodeCount() {
		return store.getNodeCount();
	}
	
	@Override
	public int getLeafCount() {
		return store.getLeafCount();
	}

	@Override
	public int getSign(int node, MDDVariable pivot) {
		return store.getSign(node, pivot);
	}

	@Override
	public boolean[] collectDecisionVariables(int node) {
		boolean[] inStore = store.collectDecisionVariables(node);
		boolean[] ret = new boolean[variables.length];
		for (int i=0 ; i<ret.length ; i++) {
			ret[i] = inStore[ custom2store[i] ];
		}
		return ret;
	}

	@Override
	public VariableEffect getVariableEffect(MDDVariable var, int node) {
		return store.getVariableEffect(var, node);
	}
	
	@Override
	public VariableEffect[] getMultivaluedVariableEffect(MDDVariable var, int node) {
		return store.getMultivaluedVariableEffect(var, node);
	}

	@Override
	public boolean isView(MDDManager ddm) {
		if (store == ddm) {
			return true;
		}
		
		if (ddm instanceof MDDManagerProxy) {
			return store == ((MDDManagerProxy)ddm).store;
		}
		
		return false;
	}

    @Override
    public String dumpMDD(int node) {
        return store.dumpMDD(node);
    }

    @Override
    public int parseDump(String s) throws ParseException {
        return store.parseDump(s);
    }
}
