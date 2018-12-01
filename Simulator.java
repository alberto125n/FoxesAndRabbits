import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

public class Simulator
{
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_DEPTH = 80;
    private static final double FOX_CREATION_PROBABILITY = 0.02;
    private static final double RABBIT_CREATION_PROBABILITY = 0.08;    

    private List<Rabbit> rabbits;
    private List<Fox> foxes;

    private Field field;
    private int step;
    private SimulatorView view;
    
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        rabbits = new ArrayList<Rabbit>();
        foxes = new ArrayList<Fox>();
        field = new Field(depth, width);

        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.orange);
        view.setColor(Fox.class, Color.blue);
        
        reset();
    }
    
    public void runLongSimulation()
    {
        simulate(4000);
    }
    
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
        }
    }
    
    public void simulateOneStep()
    {
        step++;

        List<Rabbit> newRabbits = new ArrayList<Rabbit>();        
        for(Iterator<Rabbit> it = rabbits.iterator(); it.hasNext(); ) {
            Rabbit rabbit = it.next();
            rabbit.run(newRabbits);
            if(! rabbit.isAlive()) {
                it.remove();
            }
        }
      
        List<Fox> newFoxes = new ArrayList<Fox>();
        for(Iterator<Fox> it = foxes.iterator(); it.hasNext(); ) {
            Fox fox = it.next();
            fox.hunt(newFoxes);
            if(! fox.isAlive()) {
                it.remove();
            }
        }
        
        rabbits.addAll(newRabbits);
        foxes.addAll(newFoxes);

        view.showStatus(step, field);
    }
        
    public void reset()
    {
        step = 0;
        rabbits.clear();
        foxes.clear();
        populate();
        
        view.showStatus(step, field);
    }
    
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    foxes.add(fox);
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    rabbits.add(rabbit);
                }
            }
        }
    }
}
