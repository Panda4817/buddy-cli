package dev.kmunton.buddy.styling;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

public class TableUtils {

    private TableUtils() {}

    public static String renderLightTable(TableModel model) {
        Table table = new TableBuilder(model)
                .addFullBorder(BorderStyle.fancy_light)
                .build();
        return table.render(100);
    }
}
