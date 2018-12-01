import java.util.List;
import java.util.Iterator;
import java.util.Random;

public class Fox
{
    private static final int BREEDING_AGE = 15;

    private static final int MAX_AGE = 150;
    private static final double BREEDING_PROBABILITY = 0.08;
    private static final int MAX_LITTER_SIZE = 2;
    private static final int RABBIT_FOOD_VALUE = 9;
    private static final Random rand = Randomizer.getRandom();
    
    private int age;
    private boolean alive;
    private Location location;
    private Field field;
    private int foodLevel;

    public Fox(boolean randomAge, Field field, Location location)
    {
        age = 0;
        alive = true;
        this.field = field;
        setLocation(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
    }
    
    public void hunt(List<Fox> newFoxes)
    {
        incrementAge();
        incrementHunger();
        if(alive) {
            giveBirth(newFoxes);            
            Location newLocation = findFood();
            if(newLocation == null) { 
                newLocation = field.freeAdjacentLocation(location);
            }
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                setDead();
            }
        }
    }

    public boolean isAlive()
    {
        return alive;
    }

    public Location getLocation()
    {
        return location;
    }
    
    private void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    private Location findFood()
    {
        List<Location> adjacent = field.adjacentLocations(location);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    foodLevel = RABBIT_FOOD_VALUE;
                    // Remove the dead rabbit from the field.
                    return where;
                }
            }
        }
        return null;
    }
    
    private void giveBirth(List<Fox> newFoxes)
    {
        List<Location> free = field.getFreeAdjacentLocations(location);
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fox young = new Fox(false, field, loc);
            newFoxes.add(young);
        }
    }
        
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    private void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
}
