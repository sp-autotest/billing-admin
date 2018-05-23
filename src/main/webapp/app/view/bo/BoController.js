Ext.define('BSP.view.bo.BoController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.bo',

    requires:[
        'Ext.form.action.StandardSubmit'
    ],

    onUploadBoFile: function() {
        var form = this.lookupReference('boUploadForm');
        var grid = this.lookupReference('boGrid');
        var store = grid.getStore();

        form.submit({
            waitMsg: 'Загрузка файла...',
            success: function(fp,o) {
                var bo = o.result;
                store.add(bo);
                store.sort('createdDate','desc');
            },
            failure: function(fp,o) {
                var bo = o.result;
                Ext.MessageBox.alert('Ошибка загрузки', 'Ошибка загрузки бо файла: "' + bo.fileName + '. Причина: ' + bo.text);
            }
        });
    },

    onFindButton: function() {
        var boFindForm = this.lookupReference('boFindForm');
        var grid = this.lookupReference('boGrid');
        var store = grid.getStore();

        if ( boFindForm.isValid() ) {
            boFindForm.submit({
                submitEmptyText: false,
                waitMsg: 'Выполняется поиск...',
                success: function(fp,o) {
                    store.removeAll();
                    store.add(o.result.children);
                    store.sort('createdDate','desc');
                },
                failure: function(fp,o) {
                    Ext.MessageBox.alert('Error','Произошла ошибка');
                }
            });
        }
    },

    onResetButton: function() {
        var boFindForm = this.lookupReference('boFindForm');
        boFindForm.reset();
        var grid = this.lookupReference('boGrid');
        var store = grid.getStore();
        store.removeAll();
        store.load();
    },

    onItemClick: function(grid) {
        var store = grid.getStore();
        var downloadBoBtn = Ext.getCmp('download-bo-btn');
        var selectedRows = grid.getSelectionModel().getSelection();
        if ( 1 == selectedRows.length ) {
            downloadBoBtn.setDisabled(false);
        }
    },

    onSelectedClick: function(grid, selected, eOpts) {
        if ( null != selected && selected.length == 0 ) {
            var downloadBoBtn = Ext.getCmp('download-bo-btn');
            downloadBoBtn.setDisabled(true);
        }
    },

    onDownloadBoBtn: function() {
        var grid = this.lookupReference('boGrid');
        var selection = grid.getSelection();
        var selectRow = selection[0];
        var fileName = selectRow.data.fileName;

        var downloadForm = this.lookupReference('downloadBoForm');
        downloadForm.getForm().setValues({
            'fileName' : fileName
        });
        downloadForm.submit();
    }

});

