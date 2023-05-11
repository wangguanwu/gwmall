/**
 * Storage封装
 */
const  STORAGE_KEY = 'mall';
const storage = {
    // 存储值
    setItem: function (key,value,module_name){
        if (module_name){
            let val = this.getItem(module_name);
            if (!val) {
                val = {}
            }
            val[key] = value;
            this.setItem(module_name, val);
        }else{
            let val = this.getStorage();
            val[key] = value;
            window.sessionStorage.setItem(STORAGE_KEY, JSON.stringify(val));
        }
    },
    // 获取某一个模块下面的属性user下面的userName
    getItem: function (key,module_name){
        if (module_name){
            let val = this.getItem(module_name);
            if(val) return val[key];
        }
        return this.getStorage()[key];
    },
    getStorage: function (){
        return JSON.parse(window.sessionStorage.getItem(STORAGE_KEY) || '{}');
    },
    clear: function(key, module_name){
        let val = this.getStorage();
        if (module_name){
            if (!val[module_name])return;
            delete val[module_name][key];
        }else{
            delete val[key];
        }
        window.sessionStorage.setItem(STORAGE_KEY, JSON.stringify(val));
    }
}