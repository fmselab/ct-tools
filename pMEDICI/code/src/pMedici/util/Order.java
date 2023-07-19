package pMedici.util;

/**
 * The order of parameters during tuple extraction 
 */
public enum Order {
	// Consider the params as declared
	AS_DECLARED,
	// First, consider the larger domains, then the smallest
	IN_ORDER_SIZE_DESC,
	// First, consider the smallest domains, then the largest
	IN_ORDER_SIZE_ASC,
	// Consider the params in random order
	RANDOM
}
