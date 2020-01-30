package edu.southwestern.tasks.mario.level;

import ch.idsia.mario.engine.level.SpriteTemplate;
import ch.idsia.mario.engine.sprites.Enemy;
import static edu.southwestern.tasks.mario.level.LevelParser.getEnemySprite;
import static edu.southwestern.tasks.mario.level.LevelParser.tilesAdv;
import static edu.southwestern.tasks.mario.level.LevelParser.tilesMario;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.State;

public class MarioState extends State<MarioState.MarioAction> {

	public static Heuristic<MarioAction,MarioState> moveRight = new Heuristic<MarioAction,MarioState>() {
		@Override
		public double h(MarioState s) {
			return s.level.get(0).size() - s.marioX;
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
	public int marioX; // Should use getters for these instead
	public int marioY;
	
	public MarioState(ArrayList<List<Integer>> level, int jumpVelocity, int marioX, int marioY) {
		this.level = level;
                this.level = this.preprocessLevel(level);
                /*int height = level.size();
                int width = level.get(0).size();
                for(int y=0; y<height; y++){
                    for(int x=0; x<width; x++){
                        System.out.print(this.tileAtPosition(x, y));
                    }
                    System.out.print("\n");
                }
                System.out.println();*/
		this.jumpVelocity = jumpVelocity;
		this.marioX = marioX;
		this.marioY = marioY;
	}
	
	public MarioState(ArrayList<List<Integer>> level) {
		this(level, 0, 0, level.size() - 2);
	}
	
        private ArrayList<List<Integer>> preprocessLevel(ArrayList<List<Integer>> input){
            ArrayList<List<Integer>> level = new ArrayList<>(input);
            int height = level.size();
            int width = level.get(0).size();
            for(int y=height-1; y>=0; y--){
                for(int x=width-1; x>=0; x--){
                    int tile = this.tileAtPosition(x,y);
                    if((tile == 6 || tile == 7 || tile == 8) && (y+1<height && this.tileAtPosition(x, y+1) == 2)){
                        this.setTileAtPosition(level, x, y+1, tile);
                        for(int i=y+2; i<height; i++){
                            if(this.tileAtPosition(x, i)==2){
                                this.setTileAtPosition(level, x, i, tile);
                            }else{
                                break;
                            }
                        }
                    }
                }
            }            
            return level;
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
        
        private void setTileAtPosition(ArrayList<List<Integer>> level, int x, int y, int tile){
            List<Integer> newRow = level.get(y);
            newRow.set(x, tile);
            level.set(y, newRow);
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
		return x == level.get(0).size()-1;
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
                if(!inBounds(newMarioX, newMarioY)){
                    return null;
                }
		return new MarioState(level, newJumpVelocity, newMarioX, newMarioY);
	}

	@Override
	public ArrayList<MarioAction> getLegalActions(State<MarioAction> s) {
		ArrayList<MarioAction> possible = new ArrayList<MarioAction>();
                for(MarioAction.DIRECTION act: MarioAction.DIRECTION.values()){
                    if(s.getSuccessor(new MarioAction(act))!=null){
                        possible.add(new MarioAction(act));
                    }
                }
		/*// About to fall off bottom edge: no actions
		if(!inBounds(marioX+1,marioY)) return possible;
		// Can move right if it is the goal or possable
		if(inBounds(marioX+1,marioY) && (isGoal(marioX+1) || passable(marioX+1,marioY))) possible.add(new MarioAction(MarioAction.DIRECTION.RIGHT));
		// Can move left if passable
		if(inBounds(marioX-1,marioY) &&  passable(marioX-1,marioY)) possible.add(new MarioAction(MarioAction.DIRECTION.RIGHT));
		// Can jump if on ground
		if(inBounds(marioX,marioY-1) && passable(marioX,marioY-1) && !passable(marioX,marioY+1)) possible.add(new MarioAction(MarioAction.DIRECTION.JUMP));*/
		
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
