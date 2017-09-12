package Outil;

//~--- non-JDK imports --------------------------------------------------------

import dominoExpress.Editor;

/**
 *
 * @author Nkplizz
 */
public interface Outil {

    public abstract void OnLeftClic(Editor app);

    public abstract void OnUpdate(Editor app);

    public abstract void OnRightClic(Editor app);

    public abstract void OnEscape(Editor app);

    public abstract void OnPressR(Editor app);
    
    public abstract void OnPressSpace(Editor app);

    public abstract String getType();
    
    public abstract String getHelp();

    public abstract void delete(Editor app);

    public abstract Object get(String atribut);

}
