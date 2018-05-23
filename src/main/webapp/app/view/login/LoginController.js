/**
 * This View Controller is associated with the Login view.
 */
Ext.define('BSP.view.login.LoginController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login',
    
    loginText: 'Logging in...',

    onSpecialKey: function(field, e) {
        if (e.getKey() === e.ENTER) {
            this.doLogin();
        }
    },
    
    onLoginClick: function() {
        this.doLogin();
    },

    requires: [
        'BSP.model.User'
    ],
    
    doLogin: function() {
        console.log('debug');
        var form = this.lookupReference('form');

        if (form.isValid()) {
            Ext.getBody().mask(this.loginText);

            if (!this.loginManager) {
                this.loginManager = new BSP.LoginManager({
                    session: this.getView().getSession(),
                    model: 'User'
                });
            }
            console.log("form values = " + form.getValues()[0] + form.getValues[1]);
            this.loginManager.login({
                data: form.getValues(),
                scope: this,
                success: 'onLoginSuccess',
                failure: 'onLoginFailure'
            });
        }
    },
    
    onLoginFailure: function() {
        // Do something
        Ext.getBody().unmask();
    },

    onLoginSuccess: function(user) {
        Ext.getBody().unmask();
        console.log("this.getView() = " + this.getView());
        console.log("user = " + user.username + ":" + user.password);
        this.fireViewEvent('login', this.getView(), user, this.loginManager);
    }
});
