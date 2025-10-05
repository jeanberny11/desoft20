// Create by Genesis Santana F. 16/03/2017 --JavaScript and Android Interface, Sdk FastApp Inc.

//Version AndroidSdk 1.0 

/*

Definición de las etiquetas: TaggedText

{reset} Restablecer la configuración predeterminada.
{br} Interrupción de línea. Equivalente de nueva línea.
{b}, {/b} Establecer o borrar el estilo de fuente en negrita.
{u}, {/u} Establece o elimina el estilo de fuente de subrayado.
{i}, {/i} Establecer o eliminar el estilo de fuente en cursiva.
{s}, {/s} Establecer o borrar un estilo de fuente pequeño.
{h}, {/h} Establece o elimina el estilo de fuente alto.
{w}, {/w} Establecer o borrar el estilo de fuente amplia.
{left} Alinea el texto con el borde izquierdo del papel.
{center} Alinea el texto con el centro del papel.
{right} Alinea el texto con el borde derecho del papel.

Esta es una function JavaScript que puede incluir en su app web para abrir el menu desde android.
openMenu();

*/

var AndroidSdk = function() {};


//Para Determinar si esta conectado con la interfaces Android.
AndroidSdk.Conectado = function(){
    
   return (typeof AndroidInterfaces != 'undefined');
};


AndroidSdk.printGenericData = function(printername){
    if(AndroidSdk.Conectado()){
        console.log('Android bridge initialized');
            if(typeof printername != 'undefined') {

                AndroidInterfaces.printGenericData(printername);
            }else{

                AndroidInterfaces.printGenericData("");
            }
        }
};

//Este simpre ira de ultimo en cada impresion es quien abre la conexion con la impresora:
//Si, asigna el nombre de la impresora en blanco, entonces tomara el que aya digitado en la config. de la App Android.
AndroidSdk.OpenPrinter = function(printername){
    
    if(AndroidSdk.Conectado()){

        if(typeof printername != 'undefined') {
          
            AndroidInterfaces.OpenPrinter(printername);
        }else{
            
            AndroidInterfaces.OpenPrinter("");
        }
    }
};

//Este metodo es para resetear el printer al formato default, recomendable usar antes de mandar a imprimir:
AndroidSdk.resetPrint = function(){
    
    if(AndroidSdk.Conectado()){
        
        AndroidInterfaces.resetPrint();
    }
};


//Este metodo para imprimir texto formateado o simple:
//Puede poner el charset como segundo parametro
AndroidSdk.PrintTaggedText = function(taggedtext, charset){
    
    if(AndroidSdk.Conectado()){
        
        if(typeof charset == 'undefined' || charset.length == 0) {
          
            AndroidInterfaces.PrintTaggedText(taggedtext);
        }else{
            
            AndroidInterfaces.PrintTaggedText(taggedtext, charset);
        }
    }
};

//Este simpre ira de ultimo en cada impresion es quien abre la conexion con la impresora:
//Si, asigna el nombre de la impresora en blanco, entonces tomara el que aya digitado en la config. de la App Android.
AndroidSdk.OpenPrinter2 = function(printername){
    if(AndroidSdk.Conectado()){
        AndroidInterfaces.OpenPrinter2(printername);
    }
};

//Este metodo para imprimir texto formateado o simple:
//primer paranetro text
//segundo parametro alineacion 0=left,2=right,1=center
//tercer paramentro tamaño del text 0=normal,1=grande,2=extragrande
// cuarto parametro text en negrita true or falso
AndroidSdk.PrintTextNew = function(texto,align,size,bold){
    if(AndroidSdk.Conectado()){
        AndroidInterfaces.PrintTextNew(texto,align,size,bold);
    }
};

//Este metodo para hacer salir paper en blanco o feedpaper:
AndroidSdk.FeedLine = function(lines){

    if(AndroidSdk.Conectado()){
        AndroidInterfaces.FeedLine(lines);
    }
};

//Este metodo para imprimir el codigo de barra nuevo
AndroidSdk.Printbarcodenew = function(type,barcode){
    if(AndroidSdk.Conectado()){
        AndroidInterfaces.Printbarcodenew(type,barcode);
    }
};

//Este metodo para hacer salir paper en blanco o feedpaper:
AndroidSdk.FeedPaper = function(lines){
    
    if(AndroidSdk.Conectado()){
        
        AndroidInterfaces.FeedPaper(lines);
    }
};

AndroidSdk.printbarcode = function(barcodetype, text){

    if(AndroidSdk.Conectado()){

        AndroidInterfaces.printbarcode(barcodetype, text);
    }
};

//Para borrar el historial de navegacion.
AndroidSdk.ClearHistorial = function(){
    
    if(AndroidSdk.Conectado()){
        
        AndroidInterfaces.ClearHistorial();
    }
};

//Para cambiar el color de la barra de estado en Android.
AndroidSdk.ChangeColorStatusBar = function(colorHex){
    
    if(AndroidSdk.Conectado()){
        
        AndroidInterfaces.ChangeColorStatusBar(colorHex);
    }
};

//Para ocultar el teclado en Android.
AndroidSdk.OcultarTeclado = function(){
    
    if(AndroidSdk.Conectado()){
        
        AndroidInterfaces.OcultarTeclado();
    }
};

//Para obterner un ID o Referecia unica del dispositivo Android.
AndroidSdk.getIdDispositivo = function(){
    
    if(AndroidSdk.Conectado()){
        
       return AndroidInterfaces.getIdDispositivo();
    }
};

AndroidSdk.getLastLocation = function(){

    if(AndroidSdk.Conectado()){

       return AndroidInterfaces.getLastLocation();
    }
};

//Para aplicar las urls a cargar por defecto en la WebAppSdk en Android.
AndroidSdk.AplicarUrlsDefault = function(UrlLocal, UrlRemota){
    
    if(AndroidSdk.Conectado()){
        
       return AndroidInterfaces.AplicarUrlsDefault(UrlLocal, UrlRemota);
    }
};


//imprimir foto
AndroidSdk.printlogo = function(){

    if(AndroidSdk.Conectado()){

       return AndroidInterfaces.printlogo();
    }
};

//Enviar Imagen por whatapp
AndroidSdk.sendwatsappimage = function(imagename){

    if(AndroidSdk.Conectado()){

       return AndroidInterfaces.sendWhatsAppImage(imagename);
    }
};

//Enviar Imagen por whatapp
AndroidSdk.sendwatsappimage = function(imagename,message){

    if(AndroidSdk.Conectado()){

       return AndroidInterfaces.sendWhatsAppImage(imagename,message);
    }
};

//Para Determinar si una imagen existe...
AndroidSdk.checkImageExist = function(imagename){
    if(AndroidSdk.Conectado()){
        return AndroidInterfaces.checkImageExist(imagename);
    }
};

//Abrir pantalla para seleccionar printer
AndroidSdk.selectPrinterDevice = function(idcampo){
    if(AndroidSdk.Conectado()){
       return AndroidInterfaces.selectPrinterDevice(idcampo);
    }
};

//Este metodo paradevolver un json con los dispositivos bluetooth vinculados
AndroidSdk.getConnectedDevices = function(){
    if(AndroidSdk.Conectado()){
        return AndroidInterfaces.getConnectedDevices();
    }
};