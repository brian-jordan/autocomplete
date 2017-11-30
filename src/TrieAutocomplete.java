import java.util.*;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
 * @author Jeff Forbes
 * 
 * Project 4: Autocomplete
 * Brian Jordan
 * Novemeber 27, 2017
 */
public class TrieAutocomplete implements Autocompletor {

	/**
	 * Root of entire trie
	 */
	protected Node myRoot;

	/**
	 * Constructor method for TrieAutocomplete. Should initialize the trie
	 * rooted at myRoot, as well as add all nodes necessary to represent the
	 * words in terms.
	 * 
	 * @param terms
	 *            - The words we will autocomplete from
	 * @param weights
	 *            - Their weights, such that terms[i] has weight weights[i].
	 * @throws NullPointerException
	 *             if either argument is null
	 * @throws IllegalArgumentException
	 *             if terms and weights are different weight
	 */
	public TrieAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}
		
		if (terms.length != weights.length) {
			throw new IllegalArgumentException("terms and weights are not the same length");
		}
		
		// Represent the root as a dummy/placeholder node
		myRoot = new Node('-', null, 0);

		for (int i = 0; i < terms.length; i++) {
			if (weights[i] < 0) {
				throw new IllegalArgumentException("Negative weight "+ weights[i]);
			}
			add(terms[i], weights[i]);
		}
	}

	/**
	 * Add the word with given weight to the trie. If word already exists in the
	 * trie, no new nodes should be created, but the weight of word should be
	 * updated.
	 * 
	 * In adding a word, this method should do the following: Create any
	 * necessary intermediate nodes if they do not exist. Update the
	 * subtreeMaxWeight of all nodes in the path from root to the node
	 * representing word. Set the value of myWord, myWeight, isWord, and
	 * mySubtreeMaxWeight of the node corresponding to the added word to the
	 * correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 */
	private void add(String word, double weight) {
		if (word == null){
			throw new NullPointerException("String argument is null");
		}
		if (weight < 0){
			throw new IllegalArgumentException("Weight argument is negative");
		}
		Node current = myRoot;
		for(int k = 0; k < word.length(); k++){
			char ch = word.charAt(k);
			if (current.mySubtreeMaxWeight < weight){
				current.mySubtreeMaxWeight = weight;
			}
			if (current.children.get(ch) == null){
				current.children.put(ch, new Node(ch, current, weight));
			}
			current = current.children.get(ch);
		}
		current.myWord = word;
		current.myWeight = weight;
		current.isWord = true;
	}

	// Helper Method for navigating to a certain node based on a prefix
	public Node getToNode(String prefix){
		Node current = myRoot;
		for(int k = 0; k < prefix.length(); k++){
			if (current.children.get(prefix.charAt(k)) == null){
				return null;
			}
			current = current.children.get(prefix.charAt(k));
		}
		return current;
	}
	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in the trie with the largest weight which match the given prefix,
	 * in descending weight order. If less than k words exist matching the given
	 * prefix (including if no words exist), then the array instead contains all
	 * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
	 * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
	 * 2) should return {"air"}
	 * 
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An Iterable of the k words with the largest weights among all
	 *         words starting with prefix, in descending weight order. If less
	 *         than k such words exist, return all those words. If no such words
	 *         exist, return an empty Iterable
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public Iterable<String> topMatches(String prefix, int k) {
		if (prefix == null){
			throw new NullPointerException("Prefix argument is null");
		}
		Node current = getToNode(prefix);
		if (current == null || k == 0){
			return new LinkedList<String>();
		}
		PriorityQueue<Node> npq = new PriorityQueue<>(new Node.ReverseSubtreeMaxWeightComparator());
		PriorityQueue<Term> tpq = new PriorityQueue<>(k,new Term.WeightOrder());
		npq.add(current);
		while (npq.size() > 0){
			if (tpq.size() == k && npq.peek().mySubtreeMaxWeight < tpq.peek().getWeight()){
				break;
			}
			current = npq.remove();
			if (current.isWord){
				tpq.add(new Term(current.myWord, current.myWeight));
			}
			for(Node n : current.children.values()){
				npq.add(n);
			}
		}
		LinkedList<String> topMatches = new LinkedList<>();
		int length = tpq.size();
		for (int i = 0; i < length; i++){
			topMatches.addFirst(tpq.remove().getWord());
		}
		return topMatches;
	}

	/**
	 * Given a prefix, returns the largest-weight word in the trie starting with
	 * that prefix.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from with the largest weight starting with prefix, or an
	 *         empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 */
	public String topMatch(String prefix) {
		if (prefix == null){
			throw new NullPointerException("Prefix argument is null");
		}
		Node current = getToNode(prefix);
		if (current == null){
			return "";
		}
		while (current.myWeight != current.mySubtreeMaxWeight){
			for (char c : current.children.keySet()){
				if (current.children.get(c) != null && current.children.get(c).mySubtreeMaxWeight == current.mySubtreeMaxWeight){
					current = current.children.get(c);
				}
			}
		}
		return current.myWord;
	}

	/**
	 * Return the weight of a given term. If term is not in the dictionary,
	 * return 0.0
	 */
	public double weightOf(String term) {
		if (term == null){
			throw new NullPointerException("Prefix argument is null");
		}
		Node current = getToNode(term);
		if (current.mySubtreeMaxWeight == -1){
			return 0.0;
		}
		return current.myWeight;
	}
}
