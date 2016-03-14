package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CDataGridTemplate extends BootstrapComponentTemplateBase<CDataGrid<?>> {

    @Override
    public void doRender(CDataGrid<?> grid, BootstrapRiseCanvas<?> html) {
        innerRender(grid, html);
    }

    private <T> void innerRender(CDataGrid<T> grid, BootstrapRiseCanvas<?> html) {
        // @formatter:off
        html.bTable().TEST_NAME(grid.TEST_NAME())
          .thead()
            .fForEach(grid.getColumns(), c-> html
              .th().TEST_NAME(c.TEST_NAME())
                .fIfPresent(grid.getHeaderCell(c).component, html::render)
              ._th()
            )
          ._thead()
          .tbody()
            .fForEach(grid.getItems(), item-> html
              .tr()
                .fForEach(grid.getColumns(), column-> html.fWith(grid.getCell(column, item), cell-> html
                  .td().TEST_NAME(column.TEST_NAME())
                    .fIfPresent(cell.component, html::render)
                  ._td()
                ))
              ._tr()
            )
          ._tbody()
        ._bTable();
        // @formatter:on
    }
}
