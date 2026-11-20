package pg.jni.fold;

import pg.jni.Row;

public interface IFolder<Acc,Fin> {
    Acc init();
    Acc append(Acc acc, Row row);
    Fin finalize(Acc acc);
}
