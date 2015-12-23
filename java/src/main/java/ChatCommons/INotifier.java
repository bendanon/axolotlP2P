package ChatCommons;

/**
 * Created by Guy on 12/12/2015.
 */
public interface INotifier
{
    void ReceiveMessage(String from, String Message, boolean isKeyMessage);
}

