// Login Form

$(document).ready(function(){
  var tipomen = "pc";
  var isMobile = {
      Android: function() {
          return navigator.userAgent.match(/Android/i);
      },
      BlackBerry: function() {
          return navigator.userAgent.match(/BlackBerry/i);
      },
      iOS: function() {
          return navigator.userAgent.match(/iPhone|iPad|iPod/i);
      },
      Opera: function() {
          return navigator.userAgent.match(/Opera Mini/i);
      },
      Windows: function() {
          return navigator.userAgent.match(/IEMobile/i);
      },
      any: function() {
          return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
      }
  };
  if( isMobile.any() ){
    $('#menufactura').prop('href', '/facturasm');
    tipomen = "mobil";
  }
  $("#submitlogin").click(function(){
    var user = $('input#user').val();
    var password = $('input#password').val();

    var busq = [];
    var login = JSON.parse(localStorage.getItem('login'));
    if(login.length > 0){
        /*busq= _.filter(login, function(detalle){
          return detalle.COD_LOT == loteria;
        });*/

       busq= _.where(login, {USU_USU: user, PAS_USU: password});
       if(busq.length > 0){
          localStorage.setItem('codigo', busq[0]['cod_usu']);
          localStorage.setItem('cobrador', busq[0]['COBR_USU']);
          localStorage.setItem('printer', busq[0]['PRI_USU']);
          localStorage.setItem('nombre', busq[0]['NOM_USU']);
          localStorage.setItem('tipo', busq[0]['TIP_USU']);
          location.href = "modulos/index.html";
          
       }else{
          alertify.error('datos de acceso incorrectos');
          localStorage.setItem('codigo', "");
          localStorage.setItem('cobrador', "");
          localStorage.setItem('printer', "");
          localStorage.setItem('nombre', "");
          localStorage.setItem('tipo', "");
       }
    }


   /* $.get("login.php?user="+user+"&password="+password, function(data){
      if(data.status == "ok"){
        localStorage.setItem('codigo', data.cod);
        localStorage.setItem('cobrador', data.cobrusu);
        localStorage.setItem('printer', data.printer);
        localStorage.setItem('nombre', data.nombre);
        localStorage.setItem('tipo', data.tipo);
        if(data.menusu.trim()==""){
          location.href = "/home";
        }else{
          if(tipomen=="pc"){
            location.href = "/"+data.menusu;
          }else{
            location.href = "/"+data.menusu+'m';
          }
          
        }
        
      } else {
        alert(data.msg);
      }
    }, 'json');*/
    return false;
  });


  $.ajax({
    url: 'http://inversionesjimenez.desoftinf.com/usuarios.php',
    type: 'post',
    data: {},
    dataType: 'json',               
  })
  .done(function(data) {
    localStorage.setItem('login', JSON.stringify(data));
      //console.log(JSON.parse(localStorage.getItem('login')) );  
  })
  .fail(function(jqXHR, textStatus, errorThrown) {
    
    if (jqXHR.status === 0) {
        alertify.error('Sin concexion a internet: Trabajara de modo local.');
    } else if (jqXHR.status == 404) {
        alertify.error('Pagina no encontrada [404]');
    } else if (jqXHR.status == 500) {
        alertify.error('Error interno de el servidor [500].');
    } else if (textStatus === 'parsererror') {
        alertify.error('Requested JSON parse failed.');
    } else if (textStatus === 'timeout') {
        alertify.error('Tiempo de espera expirado');
    } else if (textStatus === 'abort') {
        alertify.error('Proceso abortado');
    } else {
        alertify.error('Uncaught Error: ' + jqXHR.responseText);
    }
  });



  $.ajax({
    url: 'http://inversionesjimenez.desoftinf.com/master.php',
    type: 'post',
    data: {},
    dataType: 'json',               
  })
  .done(function(data) {
    localStorage.setItem('master', JSON.stringify(data));
      //console.log(JSON.parse(localStorage.getItem('login')) );  
  })
  .fail(function(jqXHR, textStatus, errorThrown) {
    
    if (jqXHR.status === 0) {
        alertify.error('Sin concexion a internet: Trabajara de modo local.');
    } else if (jqXHR.status == 404) {
        alertify.error('Pagina no encontrada [404]');
    } else if (jqXHR.status == 500) {
        alertify.error('Error interno de el servidor [500].');
    } else if (textStatus === 'parsererror') {
        alertify.error('Requested JSON parse failed.');
    } else if (textStatus === 'timeout') {
        alertify.error('Tiempo de espera expirado');
    } else if (textStatus === 'abort') {
        alertify.error('Proceso abortado');
    } else {
        alertify.error('Uncaught Error: ' + jqXHR.responseText);
    }
  });
});




