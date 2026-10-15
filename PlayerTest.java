import student.TestCase;

/**
 * Tests the Player class.
 */
public class PlayerTest extends TestCase
{
    // ~ Fields ................................................................
    private Player player;

    /**
     * Sets up each test method.
     */
    public void setUp()
    {
        player = new Player();
    }


    /**
     * Tests that a new player starts with nothing.
     */
    public void testNewPlayer()
    {
        assertEquals(0, player.getScore());
        assertEquals(0, player.getStreak());
        assertEquals(0, player.getBestStreak());
        assertEquals(0, player.getInventory().size());
    }


    // ~ Score .................................................................

    /**
     * Tests that points are added to the score.
     */
    public void testAddScore()
    {
        player.addScore(100);
        player.addScore(50);

        assertEquals(150, player.getScore());
    }


    /**
     * Tests that adding zero points is allowed.
     */
    public void testAddZero()
    {
        player.addScore(0);

        assertEquals(0, player.getScore());
    }


    /**
     * Tests that negative points are rejected and the score is unchanged.
     */
    public void testAddNegativeScore()
    {
        player.addScore(100);
        try
        {
            player.addScore(-10);
            fail("Expected IllegalArgumentException for negative points");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }

        assertEquals(100, player.getScore());
    }


    // ~ Streak ................................................................

    /**
     * Tests incrementing and resetting the streak.
     */
    public void testStreak()
    {
        player.incrementStreak();
        player.incrementStreak();
        assertEquals(2, player.getStreak());

        player.resetStreak();
        assertEquals(0, player.getStreak());
    }


    /**
     * Tests that the best streak is kept after a reset.
     */
    public void testBestStreak()
    {
        player.incrementStreak();
        player.incrementStreak();
        player.incrementStreak();
        player.resetStreak();
        player.incrementStreak();

        assertEquals(1, player.getStreak());
        assertEquals(3, player.getBestStreak());
    }


    // ~ Power-ups .............................................................

    /**
     * Tests adding a power-up and checking for it.
     */
    public void testAddAndHasPowerUp()
    {
        player.addPowerUp(new PowerUpCard("PWR-1",
            PowerUpCardType.DOUBLE_POINTS));

        assertEquals(true, player.hasPowerUp(PowerUpCardType.DOUBLE_POINTS));
        assertEquals(false, player.hasPowerUp(PowerUpCardType.SKIP));
        assertEquals(1, player.getInventory().size());
    }


    /**
     * Tests that a null power-up cannot be added.
     */
    public void testAddNullPowerUp()
    {
        try
        {
            player.addPowerUp(null);
            fail("Expected IllegalArgumentException for null power-up");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }

        assertEquals(0, player.getInventory().size());
    }


    /**
     * Tests that hasPowerUp handles a null type.
     */
    public void testHasNullPowerUp()
    {
        assertEquals(false, player.hasPowerUp(null));
    }


    /**
     * Tests using a power-up that is held: it is returned and removed.
     */
    public void testUsePowerUp()
    {
        PowerUpCard doublePoints =
            new PowerUpCard("PWR-1", PowerUpCardType.DOUBLE_POINTS);
        player.addPowerUp(doublePoints);

        assertEquals(doublePoints,
            player.usePowerUp(PowerUpCardType.DOUBLE_POINTS));
        assertEquals(false, player.hasPowerUp(PowerUpCardType.DOUBLE_POINTS));
        assertEquals(0, player.getInventory().size());
    }


    /**
     * Tests using a power-up that is not held: null is returned and the
     * inventory is unchanged.
     */
    public void testUseMissingPowerUp()
    {
        player.addPowerUp(new PowerUpCard("PWR-1",
            PowerUpCardType.DOUBLE_POINTS));

        assertNull(player.usePowerUp(PowerUpCardType.SECOND_CHANCE));
        assertEquals(1, player.getInventory().size());
    }


    /**
     * Tests that using a power-up removes only one of a duplicate pair.
     */
    public void testUseOneOfTwo()
    {
        player.addPowerUp(new PowerUpCard("PWR-1", PowerUpCardType.SKIP));
        player.addPowerUp(new PowerUpCard("PWR-2", PowerUpCardType.SKIP));

        player.usePowerUp(PowerUpCardType.SKIP);

        assertEquals(true, player.hasPowerUp(PowerUpCardType.SKIP));
        assertEquals(1, player.getInventory().size());
    }
}
