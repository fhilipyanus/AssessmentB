import javafx.beans.property.SimpleIntegerProperty;

public class Model {
    private final SimpleIntegerProperty sampleVar = new SimpleIntegerProperty();

    public Model() {

    }

    public final void setSampleField(final int x) {
        this.sampleVarProperty().set(x);
    }

    public final SimpleIntegerProperty sampleVarProperty() {
        return this.sampleVar;
    }

}