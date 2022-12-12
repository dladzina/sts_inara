// получает список  и контакты  поставщиков по ЛС и в целом
function GetAccountMainSuppls(){
    var $session = $jsapi.context().session;
    if ($session.Account && $session.Account.MainSuppliers)
    {
        $session.Account.MainSuppliers = [];
        //
        $session.Account.MainSuppliers = [“aes”, “alts”, “Almaty_gaz”, “tartyp”];
        
        
        
    }
    
}
