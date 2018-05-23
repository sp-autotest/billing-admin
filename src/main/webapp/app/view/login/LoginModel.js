/**
 * This is the View Model associated to the login view.
 */
Ext.define('BSP.view.login.LoginModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.login',

    // Just some data to seed the process. This might be pulled from a cookie or other
    // in a real app.
    data: {
        defaultOrg: 1,
        username: 'Don'
    },

    stores: {
        /**
         * @property {Ext.data.Store} organizations
         * This store definition populates the Organization combobox.
         */

    }
});
