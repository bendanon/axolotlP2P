package ChatCommons;

public interface INotifier

{
    void RecieveMessage(String from, String Message,ICommManager.eMessageType messageType);
}
