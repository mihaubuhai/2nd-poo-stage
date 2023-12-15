package tops.interfaces;

import output.result.ResultGetTop5;
import tops.TopCreatorVisitor;

public interface Visitable {
    /** Metoda pentru design pattern "Visitor" */
    ResultGetTop5 accept(TopCreatorVisitor v);
}
