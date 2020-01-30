package edu.southwestern.tasks.mario.level;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.State;

public class MarioState extends State<MarioState.MarioAction> {

	public static Heuristic<MarioAction,MarioState> moveRight = new Heuristic<MarioAction,MarioState>() {
		@Override
		public double h(MarioState s) {
			return s.level.size() - s.marioX;
		}
	};
	
	public static class MarioAction implements Action {
		public enum DIRECTION {JUMP, LEFT, RIGHT};
		private DIRECTION direction;
		public MarioAction(DIRECTION d) {
			this.direction = d;
		}
		
		public DIRECTION getD() {
			return direction;
		}
		
		public boolean equals(Object other) {
			if(other instanceof MarioAction) {
				return ((MarioAction) other).direction.equals(this.direction);
			}
			return false;
		}
		
		public String toString() {
			return direction.toString();
		}
	}

	private ArrayList<List<Integer>> level;
	private int jumpVelocity;
	private int marioX;
	private int marioY;
	
	public MarioState(ArrayList<List<Integer>> level, int jumpVelocity, int marioX, int marioY) {
		this.level = level;
		this.jumpVelocity = jumpVelocity;
		this.marioX = marioX;
		this.marioY = marioY;
	}
	
	public MarioState(ArrayList<List<Integer>> level) {
		this(level, 0, 0, level.size() - 2);
	}
	
	/**
	 * Easy access to the given tile integer at given (x,y) coordinates.
	 * 
	 * @param x horizontal position from left
	 * @param y vertical position from top
	 * @return tile int at those coordinates
	 */
	private int tileAtPosition(int x, int y) {
		return level.get(y).get(x);
	}
	
	/**
	 * If the coordinates are inside of the level bounds.
	 *
	 * @param x horizontal position from left
	 * @param y vertical position from top
	 * @return true if in bounds
	 */
	private boolean inBounds(int x, int y) {
		return 0 <= y && y < level.size() && 0 <= x && x < level.get(0).size();
	}
	
	/**
	 * If the x coordinate is at right edge
	 * @param x horizontal position from left
	 * @return true if off right edge
	 */
	private boolean isGoal(int x) {
		return x == level.get(0).size();
	}
	
	/**
	 * A position is passable if it is not a blockage, and does not contain an enemy
	 * @param x horizontal position from left
	 * @param y vertical position from top
	 * @return true if location is passable
	 */
	private boolean passable(int x, int y) {
                if(!inBounds(x,y)){
                    return false;
                }
		int tile = tileAtPosition(x,y);
		return LevelParser.negativeSpaceTiles.get(tile) == 0 && LevelParser.leniencyTiles.get(tile) == 0;
	}
	
	@Override
	public State<MarioAction> getSuccessor(MarioAction a) {
		int newJumpVelocity = jumpVelocity;
		int newMarioX = marioX;
		int newMarioY = marioY;
		
		if(newJumpVelocity == 0) { // Not mid-Jump
			if(passable(newMarioX,newMarioY+1)) { // Falling
				newMarioY++; // Fall down
			} else if(a.getD().equals(MarioAction.DIRECTION.JUMP)) { // Start jump
				newJumpVelocity = 4; // Accelerate up
			} 
		}
		
		if(newJumpVelocity > 0) { // Jumping up
			if(passable(newMarioX,newMarioY-1)) {
				newMarioY--; // Jump up
				newJumpVelocity--; // decelerate
			} else {
				newJumpVelocity = 0; // Can't jump if blocked above
			}
			// TODO: Add breakable case
		}
		
		// Right movement
		if(a.getD().equals(MarioAction.DIRECTION.RIGHT) && passable(newMarioX+1,newMarioY)) {
			newMarioX++;
		}
		
		// Left movement
		if(a.getD().equals(MarioAction.DIRECTION.LEFT) && passable(newMarioX-1,newMarioY)) {
			newMarioX--;
		}
		return new MarioState(level, newJumpVelocity, newMarioX, newMarioY);
	}

	@Override
	public ArrayList<MarioAction> getLegalActions(State<MarioAction> s) {
		ArrayList<MarioAction> possible = new ArrayList<MarioAction>();
		// About to fall off bottom edge: no actions
		if(!inBounds(marioX+1,marioY)) return possible;
		// Can move right if it is the goal or possable
		if(inBounds(marioX+1,marioY) && (isGoal(marioX+1) || passable(marioX+1,marioY))) possible.add(new MarioAction(MarioAction.DIRECTION.RIGHT));
		// Can move left if passable
		if(inBounds(marioX-1,marioY) &&  passable(marioX-1,marioY)) possible.add(new MarioAction(MarioAction.DIRECTION.RIGHT));
		// Can jump if on ground
		if(inBounds(marioX,marioY-1) && passable(marioX,marioY-1) && !passable(marioX,marioY+1)) possible.add(new MarioAction(MarioAction.DIRECTION.JUMP));
		
		// Add death from enemies?
		
		return possible;
	}

	@Override
	public boolean isGoal() {
		return isGoal(marioX);
	}

	@Override
	public double stepCost(State<MarioAction> s, MarioAction a) {
		return 1;
	}
	
        @Override
        public String toString(){
            return "("+marioX + "," + marioY +")";		

        }
        
}
