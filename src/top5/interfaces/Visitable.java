package top5.interfaces;

import output.result.ResultGetTop5;
import top5.topCreatorVisitor;

public interface Visitable {
    ResultGetTop5 accept(final topCreatorVisitor v);
}
