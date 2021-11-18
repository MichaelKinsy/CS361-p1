package fa.nfa;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import fa.dfa.DFA;

/**
 * Models a NFA
 * @author MichaelKinsy
 */
public class NFA implements NFAInterface {
    private Set<NFAState> Q;
    private NFAState start;
    private Set<Character> sigma;

    public NFA(){
        this.Q = new HashSet<NFAState>();
        this.sigma = new HashSet<Character>();
    }

    @Override
	public void addStartState(String name){
        start = checkIfExists(name);
        if(start == null){
            start = new NFAState(name);
            Q.add(start);
        } else {
            System.out.println("WARNING: A state with name " + name + " already exists in the NFA");
        }
    }

	@Override
	public void addState(String name){
        NFAState state = checkIfExists(name);
        if(state == null){
            state = new NFAState(name);
            Q.add(state);
        } else {
            System.out.println("WARNING: A state with name " + name + " already exists in the NFA");
        }
    }

	@Override
	public void addFinalState(String name){
        NFAState state = checkIfExists(name);
        if(state == null){
            state = new NFAState(name,true);
            Q.add(state);
        } else {
            System.out.println("WARNING: A state with name " + name + " already exists in the NFA");
        }
    }


	@Override
	public void addTransition(String fromState, char onSymb, String toState){
                    NFAState from = checkIfExists(fromState);
                    NFAState to = checkIfExists(toState);
                    if(from == null){
                        System.err.println("ERROR: No NFA state exists with name " + fromState);
			            System.exit(2);
                    } else if(to == null){
                        System.err.println("ERROR: No NFA state exists with name " + to);
			            System.exit(2);
                    }

                    from.addTransition(onSymb,to);
                    if(!sigma.contains(onSymb) && onSymb != 'e'){
                        sigma.add(onSymb);
                    }
            }
    
    /**
     * checks if given nfastate exists
     * @param name
     * @return state
     */
    private NFAState checkIfExists(String name){
        NFAState state = null;
        for(NFAState s : Q){
            if(s.getName().equals(name)){
                state = s;
                break;
            }
        }
        return state;
    }
	
	@Override
	public  Set<NFAState> getStates(){
        return Q;
    }
	
	@Override
	public Set<NFAState> getFinalStates(){
        Set<NFAState> ret = new HashSet<NFAState>();
		for(NFAState s : Q){
			if(s.isFinal()){
				ret.add(s);
			}
        }
        return ret;
    }
	
	@Override
	public NFAState getStartState(){
        return start;
    }
	
	@Override
	public Set<Character> getABC(){
        return sigma;
    }
    
	@Override
	public DFA getDFA(){
        DFA ret = new DFA();
        LinkedList<HashSet<NFAState>> queue = new LinkedList<>();
        HashSet<NFAState> tmp = new HashSet<NFAState>();
        Set<Set<NFAState>> visited = new HashSet<Set<NFAState>>();
        
        //Add our start state as the new start state to dfa and already visited in search
        tmp.add(start);
        visited.add(tmp);
        ret.addStartState(tmp.toString());
        queue.add(tmp);

        //traverse the nfa and the queue
        while (queue.peek() != null) {
            // retrieve the set of states for first element in queue
            HashSet<NFAState> states = queue.poll();
            for (Character symbol : sigma){

                // add all elosures and states to a new transition for our dfa
                Set<NFAState> tran = new HashSet<NFAState>();
                for (NFAState state : states){
                    tran.addAll(getToState(state,symbol));
                }
                for(NFAState state : tran){
                    tran.addAll(eClosure(state));
                }
                
                // if we have not see this tranisiton discover wether it is final or not and add it to our dfa
                if(visited.contains(tran) == false){
                    boolean fState = false;
                    for (NFAState state : tran){
                        if (state.isFinal()){
                            fState = true;
                        }
                    }
                    // add new dfa state from transition
                    if(fState == true){ ret.addFinalState(tran.toString()); }
                    else{    ret.addState(tran.toString()); }
                    // check off vist and add to queue
                    visited.add(tran);
                    queue.add((HashSet<NFAState>) tran);
                }
                //now add the transition to the dfa for the new states for a given symbol
                ret.addTransition(states.toString(), symbol, tran.toString());
            }

        }
        return ret;
    }

	@Override
	public Set<NFAState> getToState(NFAState from, char onSymb){
        return from.getTo(onSymb);
    }
	
    @Override
	public Set<NFAState> eClosure(NFAState s) {
        return(followE(s,new HashSet<NFAState>()));
    }

    /**
     * recusively recovers all of the states reachable by a e tranistion for a state
     * @param s
     * @param visited
     * @return Set<NFAState>
     */
    public Set<NFAState> followE(NFAState s,Set<NFAState> visited) {
        if(!visited.contains(s)){
            visited.add(s);
            for (NFAState state : getToState(s,'e')){
                followE(state, visited);
            }
        }
        return visited;
        
    }

}