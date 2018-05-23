Ext.define('BSP.view.billing.BillingController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.billing',

    requires:[
        'Ext.form.action.StandardSubmit'
    ],

    onUploadBillingFile: function() {
        var form = this.lookupReference('billingUploadForm');
        var tree = this.lookupReference('billingTree');
        var rootNode = tree.getRootNode();
        var store = tree.getStore();
        var ths = this;

        if ( form.isValid() ) {
            form.submit({
                waitMsg: 'Загрузка файла...',
                success: function(fp,o) {
                    var billingFile = o.result;
                    rootNode.appendChild({
                        id: billingFile.id,
                        name: billingFile.name,
                        fileType: billingFile.fileType,
                        businessDate: billingFile.businessDate,
                        createdDate: billingFile.createdDate,
                        format: billingFile.format,
                        leaf: billingFile.leaf
                    });
                    store.sort('id','desc');
                    Ext.Msg.alert('Загрузка успешна', 'Файл "' + billingFile.name  + '" успешно загружен.');
                    var treePanel = ths.lookupReference('billingTree');
                    treePanel.getStore().reload();
                },
                failure: function(fp,o) {
                    var billingFile = o.result;
                    Ext.Msg.alert('Ошибка загрузки', 'Ошибка загрузки биллингового файла: "' + billingFile.name + '. Причина: ' + billingFile.text);
                }
            });
        }
    },

    onFindButton: function() {
        var billingFindForm = this.lookupReference('billingFindForm');
        var tree = this.lookupReference('billingTree');
        var rootNode = tree.getRootNode();
        var store = tree.getStore();
        var viewModel = this.getViewModel();
        if ( billingFindForm.isValid() ) {
            billingFindForm.submit({
                submitEmptyText: false,
                waitMsg: 'Выполняется поиск...',
                success: function(fp,o) {
                    rootNode.removeAll();
                    rootNode.appendChild(o.result.children);
                    tree.setTitle(viewModel.data.titleTreeGridDefault + ' / ' + o.result.children.length);
                },
                failure: function(fp,o) {
                    Ext.Msg.alert('Ошибка', 'Поисковый запрос вернул ошибку');
                }
            });
        }
    },

    onResetButton: function() {
        var form = this.lookupReference('billingFindForm');
        form.reset();
        var tree = this.lookupReference('billingTree');
        var store = tree.getStore();
        var rootNode = tree.getRootNode();
        var viewModel = this.getViewModel();
        rootNode.removeAll();
        store.load();
        tree.setTitle(viewModel.data.titleTreeGridDefault);
    },

    //http://edspencer.net/2011/02/02/proxies-extjs-4/
    onTreeItemClick: function(panel) {
        var store = panel.getStore();
        var downloadBillingBtn = Ext.getCmp('download-billing-btn');
        var convertBillingBtn = Ext.getCmp('convert-billing-btn');
        var reportBillingBtn = Ext.getCmp('report-billing-btn');
        var revertBillingBtn = Ext.getCmp('revert-billing-btn');
        var treePanel = this.lookupReference('billingTree');
        var selectedRows = treePanel.getSelectionModel().getSelection();

        downloadBillingBtn.setDisabled(true);
        convertBillingBtn.setDisabled(true);
        reportBillingBtn.setDisabled(true);
        revertBillingBtn.setDisabled(true);

        if ( 1 < selectedRows.length ) {
            downloadBillingBtn.setDisabled(true);
            convertBillingBtn.setDisabled(true);
            var disableReport = false;
            var disableConvert = false;
            var fileFormat = '';
            Ext.each(selectedRows,function(selectedItem) {
                var currentFileFormat = selectedItem.data.format;
                if (fileFormat == ''){
                    fileFormat = currentFileFormat;
                } else if (fileFormat != currentFileFormat){
                    disableConvert = true;
                }
                if ( 1 < selectedItem.data.depth ) {
                    disableReport = true;
                    disableConvert = true;
                    return false;
                }
            });
            convertBillingBtn.setDisabled(disableConvert);
            reportBillingBtn.setDisabled(disableReport);
        } else {//выбран один файл
            var selectedItem = selectedRows[0];
            var depth = selectedItem.data.depth;

            if (selectedItem.data.fileType == 'POSTING'){
                Ext.Ajax.request({
                    url: 'mvc/billing/can_revert_posting_file',
                    params: {
                        node: selectedItem.data.id
                    },
                    success: function(response){
                        var canRevertFile = Ext.JSON.decode(response.responseText).success;
                        revertBillingBtn.setDisabled(!canRevertFile);
                    }
                });
            }

            BSP.model.Billing.load(Math.abs(selectedItem.id),{
                success : function(billing) {
                    downloadBillingBtn.setDisabled(!billing.data.canDownloaded);
                    if ( 1 == depth ) {//billing
                        convertBillingBtn.setDisabled(!billing.data.canDownloaded);
                        reportBillingBtn.setDisabled(!billing.data.canDownloaded);
                    }
                }
            });
        }
    },

    onTreeSelectedClick: function(grid, selected, eOpts) {
        if ( null != selected && selected.length == 0 ) {
            var downloadBillingBtn = Ext.getCmp('download-billing-btn');
            var convertBillingBtn = Ext.getCmp('convert-billing-btn');
            var reportBillingBtn = Ext.getCmp('report-billing-btn');
            var revertBillingBtn = Ext.getCmp('revert-billing-btn');
            downloadBillingBtn.setDisabled(true);
            convertBillingBtn.setDisabled(true);
            reportBillingBtn.setDisabled(true);
            revertBillingBtn.setDisabled(true);
        }
    },

    onDownloadBillingBtn: function(panel, item) {
        var tree = this.lookupReference('billingTree');
        var selection = tree.getSelection();
        var selectRow = selection[0];
        var id = selectRow.data.id;
        var fileType = selectRow.data.fileType;
        var fileName = selectRow.data.name;

        var downloadForm = this.lookupReference('downloadBillingForm');
        downloadForm.getForm().setValues({
            'node' : id,
            'fileType' : fileType,
            'fileName' : fileName
        });
        downloadForm.submit();
    },

    onConvertBillingBtn: function(panel,item) {
        var selection = this.lookupReference('billingTree').getSelection();
        var billingFiles = [];
        Ext.each(selection, function(selectedBillingFileRow) {
            billingFiles.push(selectedBillingFileRow.raw);
        });

        var convertForm = this.lookupReference('convertBillingForm');
        convertForm.getForm().setValues({
            'billingFiles' : Ext.JSON.encode(billingFiles)
        });

        var ths = this;
        convertForm.submit({
            waitMsg: 'Конвертируем...',
            timeout: '300000',
            success: function(fp,o) {
                var billingConverterResult = o.result;
                var billingFile = billingConverterResult.billing;
                var billingConverterResultWindow = Ext.create('BSP.view.billing.BillingConverterResultWindow',{
                    billingConverterResult: o.result
                });
                billingConverterResultWindow.show();

                var simpleFiles = billingConverterResult.simpleFiles;
                Ext.each(selection, function(selectedBillingFileRow) {
                    Ext.each(simpleFiles,function(simpleFile){
                        selectedBillingFileRow.appendChild({
                            id: simpleFile.id,
                            name: simpleFile.name,
                            fileType: simpleFile.fileType,
                            businessDate: simpleFile.createdDate,
                            createdDate: simpleFile.createdDate,
                            format: null,
                            leaf: true
                        });
                    });
                });
                var treePanel = ths.lookupReference('billingTree');
                treePanel.getStore().reload();
            },
            failure: function(fp,o) {
                Ext.MessageBox.alert('Error', 'Error convert file');
            }
        });
    },

    onReportBillingBtnShowBoWindow: function(panel, item) {
        var treePanel = this.lookupReference('billingTree');
        var selectedRows = treePanel.getSelectionModel().getSelection();
        var boWindow = Ext.create('BSP.view.billing.BoWindow',{
            billingFiles: selectedRows
        });
        boWindow.show();
    },

    onRevertBillingBtn: function() {
        var treePanel = this.lookupReference('billingTree');
        var selectedRows = treePanel.getSelectionModel().getSelection();
        var file = selectedRows[0].data;
        var ths = this;
        Ext.Msg.show({
            title: 'Подтверждение',
            message: 'Аннулировать файл ' + file.name + ' ?',
            buttons: Ext.Msg.OKCANCEL,
            icon: Ext.window.MessageBox.WARNING,
            fn: function (btn) {
                if (btn != 'ok') return;
                ths.revertBillingFile(file);
            }
        });
    },

    revertBillingFile: function(file) {
        var revertBillingBtn = Ext.getCmp('revert-billing-btn');
        revertBillingBtn.setDisabled(true);

        var revertForm = this.lookupReference('revertBillingForm');
        revertForm.getForm().setValues({
            'node' : file.id
        });

        var ths = this;
        revertForm.submit({
            waitMsg: 'Аннулируем...',
            timeout: '300000',
            success: function(form, action) {
                if (!action.result.success){
                    Ext.MessageBox.alert('Ошибка', 'Ошибка аннулирования файла');
                    return;
                }
                var treePanel = ths.lookupReference('billingTree');
                treePanel.getStore().reload();
            },
            failure: function() {
                Ext.MessageBox.alert('Ошибка', 'Ошибка аннулирования файла');
            }
        });
    }

});

