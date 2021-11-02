package net.ncplanner.plannerator.discord.play.action;
import net.ncplanner.plannerator.discord.play.Action;
import net.ncplanner.plannerator.discord.play.SmoreBot;
import java.util.Random;
public class SmoreAction extends Action{
    public SmoreAction(){
        super(new Random().nextInt(4000*60)+8000*60);//8-12 minutes
    }
    @Override
    protected String getBeginMessage(){
        return "You sit down by the "+SmoreBot.getCampfire(user.getIdLong())+" and cook a S'more. This could take some time.";
    }
    @Override
    protected String getFinishedMessage(){
        return user.getAsMention()+", Your S'more is done! It looks so tasty!";
    }
    @Override
    protected String getCanceledMessage(){
        return "You stop making your S'more, leaving it by the "+SmoreBot.getCampfire(user.getIdLong())+" to burn, reduced to atoms floating through the atmosphere, never to be seen again.";
    }
    @Override
    protected String getName(){
        return "S'mores";
    }
    @Override
    public void finish(){
        SmoreBot.addSmore(user);
    }
    @Override
    public String getAction(){
        return "cooking a S'more";
    }
}