Ext.define('BSP.view.system.SystemController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.system',

    onShowUserGrid: function(panel, eOpts) {
        var store = panel.getStore();
        store.load();
    },

    onShowSystemGrid: function(panel, eOpts) {
        var store = panel.getStore();
        store.load();
    },

    onShowSystemSettingsGrid: function(panel, eOpts) {
        var store = panel.getStore();
        store.load();
    },

    onEditSystemSettingsGrid: function(editor, e, eOpts) {
        var systemSettingGrid = this.lookupReference('systemSettingsGrid');
        var store = systemSettingGrid.getStore();
        var rowIdx = e.rowIdx;
        var elem = store.data.items[rowIdx];
        var name = elem.data.name;
        var newValue = e.value;
        var oldValue = e.originalValue;
        store.update({
            params: {name: name,value: newValue,oldValue : oldValue}
        })
    }

});
