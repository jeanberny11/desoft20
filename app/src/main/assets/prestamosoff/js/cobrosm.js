
$('input, select').keydown(function(tecla){
    if (tecla.keyCode == 13) {

        $('#'+$(this).attr("data-next")).focus();
    }
});

Array.prototype.getIndexBy = function (name, value) {
    for (var i = 0; i < this.length; i++) {
        if (this[i][name] == value) {
            return i;
        }
    }
    return -1;
}



$('input, textarea').blur(function(){

    $(this).val($(this).val().toUpperCase());

});


$('#gra_cob').blur(function(){

    if($(this).val()>3){
    	$(this).val('3');
    }

});

function padLeft(nr, n, str){
    return Array(n-String(nr).length+1).join(str||'0')+nr;
}
function padRight(nr, n, str){
    return nr+Array(n-String(nr).length+1).join(str||'0');
}

$("#mon_cob").alphanum({
    allow              : '1234567890.',
    allowSpace         : false,
    allowUpper         : false,
    allowLower         : false,
    allowOtherCharSets : false
});	

montos = 0.00;
moras = 0.00;
cobrosf= [];
master= [];
cobrosbt = [];
dcobrosbt = [];
cobro = [];
texto = "";
cod_cli ="";
usuario ="";
montocob = 0.00;
moracob = 0.00;
subtotcob = 0.00;


balmoras=0;
//db = openDatabase('mydb', '1.0', 'Test DB', 2 * 1024 * 1024);
cobrosbt = JSON.parse(localStorage.getItem('cobros'));
Date.prototype.yyyymmdd = function() {
  var yyyy = this.getFullYear().toString();
  var mm = (this.getMonth()+1).toString(); // getMonth() is zero-based
  var dd  = this.getDate().toString();
  return yyyy + "-" + (mm[1]?mm:"0"+mm[0]) + "-" + (dd[1]?dd:"0"+dd[0]); // padding
};


function limpiar(){	

	selectizeprestamo[0].selectize.clear();	
	
	//$('#fec_cob').val($('#feccob').val());
	$('#gra_cob').val('');
	$('#num_cob').val('');
	$('#con_cob').val('');
	$('#mon_let').val('');
	$('#mon_cob').val('');
	$('#bal_pre').val('');
	$('#mor_cob').val('2');
	cuotas=[];
	$('.cuotas').html("");

	montos = 0.00;
	moras = 0.00;
	balmoras=0;

	var date = new Date();
	$('#fec_cob').val(date.yyyymmdd());	

}

function inhabilitarsel(){
	selectizeprestamo[0].selectize.disable();		
	$('input, textarea, select').prop('disabled', true);
}

function habilitarsel(){
	selectizeprestamo[0].selectize.enable();	
	$('input, textarea, select').prop('disabled', false);
	//$('#mor_cob').prop('disabled', true);	
}

function buscarreg (cod, orden){
		
	inhabilitarsel();
	cambio ="N";

	var busq = [];
    
    //console.log(cod);
	if(cod==1){	
		busq= _.filter(cobrosbt, function(detalle){
	      return detalle.num_cob == cobrosbt[cobrosbt.length-1]['num_cob'];
	    });
	}else{
		busq= _.filter(cobrosbt, function(detalle){
	      return detalle.num_cob == cod;
	    });
	}
	$.each(busq, function(indice, reg) {
		selectizeprestamo[0].selectize.setValue(reg['pre_cob']);
		$('#fec_cob').val(reg['fec_cob']);
		$('#gra_cob').val(reg['gra_cob']);
		$('#con_cob').val(reg['con_cob']);						
		$('#num_cob').val(reg['num_cob']);
		$('#mon_cob').val(reg['tot_cob']);
		$('#numcob').val(cobrosbt[cobrosbt.length-1]['num_cob']);
		cod_cli = reg['cli_cob'];
		usuario = reg['usu_cob'];
		montocob = reg['tot_cob'];
		moracob = reg['mora_cob'];
		subtotcob= reg['sub_tot'];
		cuotas=[];
		$('#detalle-cuotas').html("");	
			
		cuotas = reg['cuota'];
		//console.log(cuotas);
		if(cuotas.length>0){
			renderTablec('cuotas');
		}
		
		$('#num_cob').val(reg['num_cob']);
		$('#mon_let').val(NumeroALetras(parseFloat(reg['tot_cob']).toFixed(2)));
	});
    cambio ="N";
		
	
	
	cambio ="N";	
}

$(document).on('focus', '.MON_PFAC', function(){
	
	$('div.btn-group').hide('fast');
	
});
$(document).on('blur', '.MON_PFAC', function(){
	
	$('div.btn-group').show('fast');
	
});

$(document).on('blur', '#mon_cob', function(){
	if($('#pre_cob').val().trim()!='' && parseFloat($('#mon_cob').val())<=parseFloat($('#bal_pre').val())){
		if($('#mon_cob').val().trim()!=''){
			campo1 = $( ".MON_PFAC" );
			var montoapl = parseFloat($('#mon_cob').val());
			for (var i =0; i <= campo1.length - 1; i++) {
				//$(campo1[i]).val(i);
				//console.log(montoapl);
				if(montoapl>=parseFloat($(campo1[i]).attr('data-bal'))){
					$(campo1[i]).val($(campo1[i]).attr('data-bal'));
					montoapl = montoapl - parseFloat($(campo1[i]).attr('data-bal'));
				}else{
					if(montoapl>0){
						$(campo1[i]).val(montoapl);
						montoapl=0;
						$($( ".MON_PFAC" )[$( ".MON_PFAC" ).length-1]).blur();
						break;
					}else{
						//$($( ".MON_PFAC" )[$( ".MON_PFAC" ).length-1]).focus();
						$($( ".MON_PFAC" )[$( ".MON_PFAC" ).length-1]).blur();
						break;
					}
				}
				$($( ".MON_PFAC" )[$( ".MON_PFAC" ).length-1]).blur();
			}
		}
	}else{
		$('#mon_cob').val('');
	}
});	


$(document).on('blur', '.MON_PFAC', function(){
	if($(this).attr('data-orden')>1 && $(this).val()>0){		
		if($('[data-orden='+($(this).attr("data-orden")-1)+']').val().trim()=='' || parseFloat($('[data-orden='+($(this).attr("data-orden")-1)+']').val()).toFixed(2)!=$('[data-orden='+($(this).attr("data-orden")-1)+']').attr('data-bal')){
			$(this).val('');
			$('[data-orden='+($(this).attr("data-orden")-1)+']').focus();
			alert('Debe pagar la cuota mas vieja primero');
		}
	}else{
		if($(this).attr('data-orden')==1 && $(this).val()<=0){	
			$(".MON_PFAC" ).val('');
			$('#sub_tot').val('');
			$('#mora_cob').val('');
			$('#tot_cob').val('');
		}else {
			if((parseInt($(this).attr("data-orden"))+1) <= cuotas[0]['cuotas'] ){
				if($('[data-orden='+(parseInt($(this).attr("data-orden"))+1)+']').val().trim()!==''){
				$(".MON_PFAC" ).val('');
				$('#sub_tot').val('');
				$('#mora_cob').val('');
				$('#tot_cob').val('');
				}
			}
		}
	}
	sumamontos = $( ".MON_PFAC" );
	var campo = $(this);
	var step1, step2;
	$('#div_carga').addClass('cargar').removeClass('nocargar');
	if( $(this).attr('data-bal')>= $(this).val() ){		
		$('#div_carga').addClass('nocargar').removeClass('cargar');
		
		montos =0.00;
		moras =0.00;
		saldos ='SALDO CUOTA';
		abonos = 'ABONO CUOTA';
		for (var i = sumamontos.length - 1; i >= 0; i--) {
			if(parseFloat(sumamontos[i].value)>0){				
				if( parseFloat($(sumamontos[i]).val()).toFixed(2)== $(sumamontos[i]).attr('data-bal') ){
					montos=parseFloat(montos)+parseFloat(sumamontos[i].value)-parseFloat($(sumamontos[i]).attr('data-mor'));
					moras=parseFloat(moras)+parseFloat($(sumamontos[i]).attr('data-mor'));
					total=parseFloat(montos)+parseFloat(moras);
					$('#sub_tot').val(montos.toFixed(2));
					$('#mora_cob').val(moras.toFixed(2));
					$('#tot_cob').val(total.toFixed(2));
					saldos=saldos+" "+$(sumamontos[i]).attr('data-fac')+",";
					//console.log(montos);
					//console.log(parseFloat($(sumamontos[i]).val()).toFixed(2));
				}else{
					if($(sumamontos[i]).val()>parseFloat($(sumamontos[i]).attr('data-mor'))){
						montos=parseFloat(montos)+parseFloat(sumamontos[i].value)-parseFloat($(sumamontos[i]).attr('data-mor'));
						moras=parseFloat(moras)+parseFloat($(sumamontos[i]).attr('data-mor'));
						total=parseFloat(montos)+parseFloat(moras);
						$('#sub_tot').val(montos.toFixed(2));
						$('#mora_cob').val(moras.toFixed(2));
						$('#tot_cob').val(total.toFixed(2));
					}else{
						montos=parseFloat(montos)+0.00;
						moras=parseFloat(moras)+parseFloat(sumamontos[i].value);
						total=parseFloat(montos)+parseFloat(moras);
						$('#sub_tot').val(montos.toFixed(3));
						$('#mora_cob').val(moras.toFixed(3));
						$('#tot_cob').val(total.toFixed(3));
					}

					abonos=abonos+" "+$(sumamontos[i]).attr('data-fac')+",";
				}
			}
		}
		$('#mon_let').val(NumeroALetras(montos.toFixed(2)));
		saldos = saldos.substring(0, saldos.length-1);
		abonos = abonos.substring(0, abonos.length-1);
		if(saldos=='SALDO CUOTA' || saldos=='SALDO CUOT'){
			saldos='';
		}
		if(abonos=='ABONO CUOTA' || abonos=='ABONO CUOT'){
			abonos='';
		}
		if(saldos!='' && abonos==''){
			$('#con_cob').val(saldos);
		}else{
			if(saldos=='' && abonos!=''){
				$('#con_cob').val(abonos);
			}else{
				if(saldos!='' && abonos!=''){
					$('#con_cob').val(saldos+" Y "+abonos);
				}
			}
		}	
		
	}else{
		alert('Monto a pagar no puede ser mayor al balance');
		campo.val('');
		campo.focus();
	}	

		
	
});

$('#pre_cob').change(function(){
	cuotas=[];
	$('.cuotas').html("");
	$('#bal_pre').val('');
	$('#mon_cob').val('');
	$('#mon_let').val('');
	$('#con_cob').val('');
	if($('#pre_cob').val().trim() !=''){
		var prestamosf =JSON.parse(localStorage.getItem('cobros-f'));
		detalles = _.filter(prestamosf, function(detalle){
			return detalle.num_pre == $('#pre_cob').val();
		});		
		//$('#bal_cli').val(detalles[0].bal_cli);
		//console.log(prestamosf);
		cod_cli = detalles[0].cli_pre;
		$('#bal_pre').val(detalles[0].bal_pre);
		if(cambio=="S"){			
			if($('#mor_cob').val()=="2"){
				$.each(detalles[0]['cobros-f'], function(index, val) {
					detalles[0]['cobros-f'][index]['mor_cob']=0;
					detalles[0]['cobros-f'][index]['pen_cuo']=detalles[0]['cobros-f'][index]['pen_cuo2'];
				});
			}
			//console.log(detalles[0]['cobros-f']);
			$('.cuotas').html("");
			cuotas =detalles[0]['cobros-f'];
			renderTable('cuotas');
			$.each(cuotas, function(index, val) {
				balmoras +=val['mor_cob'];
				if($('#mor_cob').val()=="2"){
					$('#bal_pre').val(detalles[0].bal_pre);
				}else{
					$('#bal_pre').val(parseFloat(detalles[0].bal_pre)+balmoras);
				}
			});
			$(".MON_PFAC").alphanum({
			    allow              : '1234567890.',
			    allowSpace         : false,
			    allowUpper         : false,
			    allowLower         : false,
			    allowOtherCharSets : false
			});			        
	
		}
		
	}else{
		
	}

});



$('#mor_cob').change(function(){
	cuotas=[];
	$('.cuotas').html("");
	$('#bal_pre').val('');
	$('#mon_cob').val('');
	$('#mon_let').val('');
	$('#con_cob').val('');
	if($('#pre_cob').val().trim() !=''){
		var prestamosf =JSON.parse(localStorage.getItem('cobros-f'));
		detalles = _.filter(prestamosf, function(detalle){
			return detalle.num_pre == $('#pre_cob').val();
		});		
		$('#bal_cli').val(detalles[0].bal_cli);
		cod_cli = detalles[0].cod_cli;
		$('#bal_pre').val(detalles[0].bal_pre);
		if(cambio=="S"){			
			if($('#mor_cob').val()=="2"){
				$.each(detalles[0]['cobros-f'], function(index, val) {
					detalles[0]['cobros-f'][index]['mor_cob']=0;
					detalles[0]['cobros-f'][index]['pen_cuo']=detalles[0]['cobros-f'][index]['pen_cuo2'];
				});
			}
			//console.log(detalles[0]['cobros-f']);
			$('.cuotas').html("");
			cuotas =detalles[0]['cobros-f'];
			renderTable('cuotas');

			$.each(cuotas, function(index, val) {
				balmoras +=val['mor_cob'];
				if($('#mor_cob').val()=="2"){
					$('#bal_pre').val(detalles[0].bal_pre);
				}else{
					$('#bal_pre').val(parseFloat(detalles[0].bal_pre)+balmoras);
				}
			});
			$(".MON_PFAC").alphanum({
			    allow              : '1234567890.',
			    allowSpace         : false,
			    allowUpper         : false,
			    allowLower         : false,
			    allowOtherCharSets : false
			});			        
	
		}
		
	}else{
		
	}
});

$('#gra_cob').change(function(){
	$('.cuotas').html("");
	$('#bal_pre').val('');
	$('#mon_cob').val('');
	$('#mon_let').val('');
	$('#con_cob').val('');
	 if($(this).val()>3){
    	$(this).val('3');
    }
	if($('#pre_cob').val().trim() !=''){
		var prestamosf =JSON.parse(localStorage.getItem('cobros-f'));
		detalles = _.filter(prestamosf, function(detalle){
			return detalle.num_pre == $('#pre_cob').val();
		});		
		$('#bal_cli').val(detalles[0].bal_cli);
		cod_cli = detalles[0].cod_cli;
		$('#bal_pre').val(detalles[0].bal_pre);
		if(cambio=="S"){			
			if($('#mor_cob').val()=="2"){
				$.each(detalles[0]['cobros-f'], function(index, val) {
					detalles[0]['cobros-f'][index]['mor_cob']=0;
					detalles[0]['cobros-f'][index]['pen_cuo']=detalles[0]['cobros-f'][index]['pen_cuo2'];
				});
			}else{

				$.each(detalles[0]['cobros-f'], function(index, val) {
					if(detalles[0]['cobros-f'][index]['dias_cuo']>0){
						var moracc = detalles[0]['cobros-f'][index]['mor_cob']/detalles[0]['cobros-f'][index]['dias_cuo'];
						detalles[0]['cobros-f'][index]['mor_cob']=detalles[0]['cobros-f'][index]['mor_cob']-(moracc*$('#gra_cob').val() );
						detalles[0]['cobros-f'][index]['pen_cuo']=detalles[0]['cobros-f'][index]['pen_cuo']-(moracc*$('#gra_cob').val() );
					}
				});
			}
			//console.log(detalles[0]['cobros-f']);
			$('.cuotas').html("");
			cuotas =detalles[0]['cobros-f'];
			renderTable('cuotas');

			$.each(cuotas, function(index, val) {
				if(parseFloat(val['mor_cob'])>0){
					balmoras +=parseFloat(val['mor_cob']);
					console.log(balmoras);
				}
				
				if($('#mor_cob').val()=="2"){
					$('#bal_pre').val(detalles[0].bal_pre);
				}else{
					$('#bal_pre').val(parseFloat(detalles[0].bal_pre)+balmoras);
				}
			});
			$(".MON_PFAC").alphanum({
			    allow              : '1234567890.',
			    allowSpace         : false,
			    allowUpper         : false,
			    allowLower         : false,
			    allowOtherCharSets : false
			});			        
	
		}
		
	}else{
		
	}
});

$(document).on('click', '#btn-salvar', function (event) {
	op = $('.MON_PFAC');
	var monrec = 0.00;
	detallesf =[];

	for (var i = 0; i < op.length; i++) {
		if($(op[i]).val()>0){
			detallef = {};
			if(op[i].value.trim()!==''){				
				if($(op[i]).val()==$(op[i]).attr('data-bal')){									
					detallef.cuo_cuo=$(op[i]).attr('data-fac');
					detallef.sub_tot=parseFloat(op[i].value)-parseFloat($(op[i]).attr('data-mor'));
					detallef.mor_cob=parseFloat($(op[i]).attr('data-mor'));
					detallef.fec_cuo=$(op[i]).attr('data-fec');
					detallef.pen_cuo=parseFloat($(op[i]).attr('data-bal'));
					detallef.num_pre=$('#pre_cob').val();
					detallef.num_cob=parseInt(cobrosbt[cobrosbt.length-1]['num_cob'])+1;
					detallef.tot_cob=parseFloat(op[i].value);

				}else{
					if($(op[i]).val()>parseFloat($(op[i]).attr('data-mor'))){
						detallef.cuo_cuo=$(op[i]).attr('data-fac');
						detallef.sub_tot=parseFloat(op[i].value)-parseFloat($(op[i]).attr('data-mor'));
						detallef.mor_cob=parseFloat($(op[i]).attr('data-mor'));
						detallef.fec_cuo=$(op[i]).attr('data-fec');
						detallef.pen_cuo=parseFloat($(op[i]).attr('data-bal'));
						detallef.num_pre=$('#pre_cob').val();
						detallef.num_cob=parseInt(cobrosbt[cobrosbt.length-1]['num_cob'])+1;
						detallef.tot_cob=parseFloat(op[i].value);						
					}else{
						detallef.cuo_cuo=$(op[i]).attr('data-fac');
						detallef.sub_tot=0.00;
						detallef.mor_cob=parseFloat(op[i].value);
						detallef.fec_cuo=$(op[i]).attr('data-fec');
						detallef.pen_cuo=parseFloat($(op[i]).attr('data-bal'));
						detallef.num_pre=$('#pre_cob').val();
						detallef.num_cob=parseInt(cobrosbt[cobrosbt.length-1]['num_cob'])+1;
						detallef.tot_cob=parseFloat(op[i].value);
					}					
				}
			}
			detallef.num_pre=$("#pre_cob").val();
			detallef.bal_cuo=parseFloat($(op[i]).attr('data-bal'))-parseFloat($(op[i]).attr('data-mor'));
			detallesf.push(detallef);
		}
	}
	var post = {};
	if ($("#num_cob").val().trim()=="") {
		if ($("#pre_cob").val().trim()!=="") {
			if ((montos+moras)>0) {

				var dt = new Date();
				var timepp = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
				
				post.num_cob = parseInt(cobrosbt[cobrosbt.length-1]['num_cob'])+1;
				post.num_cob= padLeft(post.num_cob,7,'0');
				post.fec_cob = $('#fec_cob').val();
				post.hor_cob = timepp;
				post.con_cob = $('#con_cob').val();
				post.pre_cob = $('#pre_cob').val();
				post.cli_cob = cod_cli;					
				post.cobr_cob = localStorage.getItem('cobrador');						
				post.sub_tot = montos;
				post.mora_cob = moras;
				post.tot_cob = montos+moras;
				post.mor_cob = $('#mor_cob').val();
				post.gra_cob = $('#gra_cob').val();
				post.fpag_cob = "1";
				post.usu_cob = localStorage.getItem('nombre');
				post.des_cob = "0.00";

				post.not_cob = "";
				post.npag_cob = "";
				post.bpag_cob = "";
				post.pos_cob = "N";
				post.ref_cob = "CL";
				post.cuota=detallesf;

				subtotcob = montos;
				moracob=moras;
				montocob=montos+moras;
				$('#div_carga').addClass('cargar').removeClass('nocargar');
				
				cobrosbt.push(post);
				localStorage.setItem('cobros', JSON.stringify(cobrosbt));
				inhabilitarsel();
				$("#num_cob").val(post.num_cob);
				cuotas=[];
				$('#detalle-cuotas').html("");	
					
				cuotas = detallesf;
				//console.log(cuotas);
				if(cuotas.length>0){
					renderTablec('cuotas');
				}

				//padLeft("94",7,'0')
				var prestamosf =JSON.parse(localStorage.getItem('prestamos'));
				var pp = _.findLastIndex(prestamosf, {num_pre: $('#pre_cob').val()});
				if(pp>=0){
					prestamosf[pp]['bal_pre'] = parseFloat(prestamosf[pp]['bal_pre'])-parseFloat(montos);
					prestamosf[pp]['bal_cli'] = parseFloat(prestamosf[pp]['bal_cli'])-parseFloat(montos);
					localStorage.setItem('prestamos', JSON.stringify(prestamosf));
				}				

				var cobrosf =JSON.parse(localStorage.getItem('cobros-f'));
				var pp = _.findLastIndex(cobrosf, {num_pre: $('#pre_cob').val()});
				if(pp>=0){
					cobrosf[pp]['bal_pre'] = parseFloat(cobrosf[pp]['bal_pre'])-parseFloat(montos);					
					
					
					var cuote = cobrosf[pp]['cobros-f'];
					$.each(detallesf, function(index, val) {
						
						var pl = _.findLastIndex(cobrosf[pp]['cobros-f'], {cuo_cuo: val['cuo_cuo'] });
						if(parseFloat(val['sub_tot']) >=   cobrosf[pp]['cobros-f'][pl]['bal_cuo'] ){
							cobrosf[pp]['cobros-f'] = _.filter(cobrosf[pp]['cobros-f'], function(detalle){
								return detalle.cuo_cuo !=val['cuo_cuo'] ;
							});	
						}else{ 								
							cobrosf[pp]['cobros-f'][pl]['bal_cuo'] = parseFloat(cobrosf[pp]['cobros-f'][pl]['bal_cuo'])-parseFloat(val['sub_tot']);
							cobrosf[pp]['cobros-f'][pl]['pen_cuo'] = parseFloat(cobrosf[pp]['cobros-f'][pl]['pen_cuo'])-parseFloat(val['sub_tot']);
							cobrosf[pp]['cobros-f'][pl]['pen_cuo2'] = parseFloat(cobrosf[pp]['cobros-f'][pl]['pen_cuo2'])-parseFloat(val['sub_tot']);
						}
						
					});
					localStorage.setItem('cobros-f', JSON.stringify(cobrosf));
					document.getElementById("print-pv").click();

				}
					
			} else {
				alert('Debe pagar o abonar minimo 1 cuota');
				
			}	
		} else {
			alert('Debe elegir un prestamo');
			selectizeprestamo[0].selectize.focus();
		}
	}	
});


$('#btn-inicio').click(function () {
	if ($("#num_cob").val().trim()!=="") {
		if(parseInt($('#num_cob').val())>1){
			buscarreg('1', 'ASC');
		}
	}
});

$('#btn-atras').click(function () {
	if ($("#num_cob").val().trim()!=="") {
		if(parseInt($('#num_cob').val())>1){
			buscarreg(parseInt($('#num_cob').val())-1, 'ASC');
		}
	}
});

$('#btn-adelante').click(function () {
	if ($("#num_cob").val().trim()!=="") {
		if(parseInt($('#num_cob').val())<parseInt($('#numcob').val())){
			buscarreg(parseInt($('#num_cob').val())+1, 'ASC');
		}
	}
});

$('#btn-fin').click(function () {
	if ($("#num_cob").val().trim()!=="") {
		if(parseInt($('#num_cob').val())<parseInt($('#numcob').val())){
			buscarreg('1', 'DESC');
		}
	}
});

$('#btn-nuevo').click(function () {

	prestamo2 = JSON.parse(localStorage.getItem('prestamos'));
	prestamo2 = _.filter(prestamo2, function(detalle){
		return detalle.bal_pre > 0 ;
	});	
	
	selectizeprestamo[0].selectize.destroy();
	prestamo = prestamo2;
	selectizeprestamo = $('#pre_cob').selectizeThis({
		maxItems   : 1,
		valueField : 'num_pre',
		labelField : 'nom_cli',
		searchField: 'searc_field',
		options    : prestamo,
		create     : false,
		maxOptions : 10
	});
	limpiar();
	habilitarsel();	
	
	var master = JSON.parse(localStorage.getItem('master'));

	if(master.length>0){
		$('#mor_cob').val(master[0]['MORA']);
		$('#mor_cob').prop('disabled', true);

	}
	cambio ="S";
});

$('#btn-cancelar').click(function () {
	if ($("#num_cob").val().trim()=="") {
		prestamo2 = JSON.parse(localStorage.getItem('prestamos'));
		prestamo2 = _.filter(prestamo2, function(detalle){
			return detalle.bal_pre > 0 ;
		});	
		
		selectizeprestamo[0].selectize.destroy();
		prestamo = prestamo2;
		selectizeprestamo = $('#pre_cob').selectizeThis({
			maxItems   : 1,
			valueField : 'num_pre',
			labelField : 'nom_cli',
			searchField: 'searc_field',
			options    : prestamo,
			create     : false,
			maxOptions : 10
		});
		limpiar();
		inhabilitarsel();
		buscarreg('1', 'DESC');	
		
	}
	
});
$('#print-ca').click(function(){
	//alertify.error("dd");
	if($('#num_cob').val().trim()!==""){
		
		$('.msgimp').fadeOut('slow');
	}
});
$('#print-8').click(function(){
	//alertify.error("dd");
	var master = JSON.parse(localStorage.getItem('master'));
	if($('#num_cob').val().trim()!==""){
		$('.msgimp').fadeOut('slow');
		texto = "{center}{b}{h} {/h}{br}";
		texto += "{center}{b}{h}"+master[0]['nom_sys']+"{/h}{br}";
		texto += "{center}{b}"+master[0]['dir_sys']+"{br}";
		texto += "{center}{b}"+master[0]['tel1_sys']+" / "+master[0]['tel2_sys']+"{br}";
		texto += "{center}{b}COMPROBANTE DE PAGO{br}";
		texto += "{center}----------------------------{br}";
		texto += "{left}{b}No. Recibo:"+$('#num_cob').val()+"{br}";
		texto += "{left}{b}Cliente:"+$('.prestamo-select-col .item').html().split("-")[1].trim()+"{br}";
		texto += "{left}{b}Prestamo:"+padLeft($('#pre_cob').val(),10,'0')+"{br}";
		
		texto += "{left}{b}Fecha: "+$('#fec_cob').val()+" {br}";
		texto += "{left}{b}Cajero:"+localStorage.getItem('nombre')+"{/b}{br}";
		//Heder productos
		
		texto += "{center}----------------------------{br}";
		texto += "{left}{b}Cuota      Monto     Balance{/b}{br}";
		texto += "{center}----------------------------{br}";
		//Detalle productos
		$.each(cuotas, function(indice, fac) {
			texto += "{left}"+padRight(fac['cuo_cuo'],6,' ')+padLeft(fac['sub_tot'],10,' ')+padLeft(parseFloat(fac['bal_cuo']-fac['sub_tot']).toFixed(2),10,' ')+"{br}";
			
			//texto += "{reset}{left}"+padRight(fac['NUM_FAC'],20,' ')+'    '+fac['MON_PAG']+"{br}";
		});
		//Crear Pie del recibo
		texto += "{center}----------------------------{br}";
		texto += "{left}"+$('#con_cob').val()+"{br}";
		texto += "{left}"+$('#mon_let').val()+"{br}";
		texto += "{right}============================{br}";
		texto += "{right}{b}Sub Total:"+padLeft(parseFloat(subtotcob).toFixed(2),12,' ')+"  {br}";
		texto += "{right}{b}Mora:"+padLeft(parseFloat(moracob,).toFixed(2),12,' ')+"  {br}";
		texto += "{right}{b}Total Pagado:"+padLeft(parseFloat(montocob).toFixed(2),12,' ')+"  {br}";
		texto += "{right}{b}Balance:"+padLeft($('#bal_pre').val(),12,' ')+"  {br}";
		texto += "{center}PAGOS CON RETRASO GENERAN MORA{br}";
		texto += "{center}Gracias por preferirnos{br}";
		texto += "{br}";

		if(AndroidSdk.Conectado()){
			var printerp = localStorage.getItem('printer');
			//Este metodo es para resetear el printer al formato default, recomendable usar antes de mandar a imprimir:
			AndroidSdk.resetPrint();
			//Este metodo para imprimir texto formateado o simple:
			AndroidSdk.PrintTaggedText(texto);
			//Este metodo para hacer salir paper en blanco o feedpaper:
			AndroidSdk.FeedPaper(100);
			//Este simpre ira de ultimo en cada impresion es quien abre la conexion con la impresora:
			AndroidSdk.OpenPrinter(printerp);
		}else{
			//Aqui es por si quieren capturar cuando no existe la conexion con la interfaces con android, para usar otro metodo de impresion puede ser para una alerta o imprimir por: window.print();
			alert('AndroidPrint no existe.');
		}
	}
});

$('#print-pv').click(function(){
	$(".recibo").addClass('centror');
	var master = JSON.parse(localStorage.getItem('master'));
	if($('#num_cob').val().trim()!==""){
		$("#linom_sys").html(master[0]['nom_sys']);
		$("#lidir_sys").html(master[0]['dir_sys']);
		$("#litel_sys").html(master[0]['tel1_sys']+" / "+master[0]['tel2_sys']);
		
		$("#linum_cob").html($('#num_cob').val());
		$("#linom_cli").html($('.prestamo-select-col .item').html().split("-")[1].trim());
		$("#lipre_cob").html(padLeft($('#pre_cob').val(),10,'0'));
		$("#lifec_cob").html($('#fec_cob').val());
		$("#liusu_cob").html(localStorage.getItem('nombre'));
		
		var detallesr ="";
		$.each(cuotas, function(indice, fac) {
			
			detallesr+='<ul style="text-align: right;width:90%;" class="font2">';
			detallesr+='<li style="font-weight: bold;width:20%">'+fac['cuo_cuo']+'</li>';
			detallesr+='<li style="font-weight: bold;width:36%">'+fac['sub_tot']+'</li>';
			detallesr+='<li style="font-weight: bold;width:36%">'+parseFloat(fac['bal_cuo']-fac['sub_tot']).toFixed(2)+'</li>';
			detallesr+='</ul>';
			$(".detallesli").html(detallesr);
		});
		$("#licon_cob").html($('#con_cob').val());
		$("#limon_let").html($('#mon_let').val());

		$("#lisub_tot").html(parseFloat(subtotcob).toFixed(2));
		$("#limor_cob").html(parseFloat(moracob).toFixed(2));
		$("#litot_cob").html(parseFloat(montocob).toFixed(2));
		$("#libal_pre").html($('#bal_pre').val());
		
		html2canvas($(".crear"), {
          	onrendered: function(canvas) {
	            theCanvas = canvas;
	           // console.log(canvas.toDataURL() );

	            $("#desca").attr('href', canvas.toDataURL() );
				$("#desca").attr('download', 'recibo'+$('#num_cob').val()+'.png' );
				document.getElementById("desca").click();
				$(".recibo").removeClass('centror');
				$('.msgimp').fadeOut('slow');
				if(AndroidSdk.Conectado()){
		            setTimeout(function() {
		            	//alert(datam);
				    	if(AndroidSdk.checkImageExist($('#imagenrecibo').val())){
				    		alertify.success('Imagen existe!');
			                AndroidSdk.sendwatsappimage($('#imagenrecibo').val());
			            }else{
			            	alertify.success('esperando descarga');
			                setTimeout(function() {
						    	if(AndroidSdk.checkImageExist($('#imagenrecibo').val())){
					                AndroidSdk.sendwatsappimagem($('#imagenrecibo').val(), 'Ticket');
					                alertify.success('Imagen existe!');
					            }else{
					                alertify.error('Imagen no existe!');
					            }
								
							}, 1000);
			            }
						
					}, 1500);
		        }
                                  
          	}
      	});
		//
	}
});

$('#btn-imprimir').click(function(){
	//alert("dd");
	if($('#num_cob').val().trim()!==""){
		$('.msgimp').fadeIn('slow');
				
	}
});



function saveDetalle() {

	renderTable('cuotas');

}

function renderTable(data){

	var template = Handlebars.compile( $('#CuotasMasterTemplate').html() );
	$('.'+data).html( template(this) );
	
}

function renderTablec(data){

	var template = Handlebars.compile( $('#Cuotas2MasterTemplate').html() );
	$('.'+data).html( template(this) );
	
}




function buscarcob(){
	$.ajax({
		url: 'cobros-bt',
		type: 'post',
		data: {},
		dataType: 'json',
		success: function (data) {

			var numrow = data[0]['NUM_ROWS'] /30;
			$('#pageselectorf').html('');
			numfil = 0;
			for (var i = 1; i < numrow+1; i++) {
				if(i==1){
					$('#pageselectorf').html($('#pageselectorf').html()+'<option selected="selected" style="font-weight: bold" value="0">'+i+'</option>');
				}else{
					numfil = numfil+30;
					$('#pageselectorf').html($('#pageselectorf').html()+'<option value="'+numfil+'">'+i+'</option>');
				}
			}
			cobros = data;
			saveDetalleF();
		}
	});
}


$(document).on('click', '#btn-fac', function(){
	$('.panel-fac').show();
	$('#likef').prop('disabled', false);
	buscarcob();
	$('#likef').val('');
	$('#likef').focus();

	
});

$('#likef').keydown(function(tecla){
	if (tecla.keyCode == 13) {
		$.ajax({
			url: 'cobros-bt',
			type: 'post',
			data: {desde: $("#pageselectorf").val(), limit: $("#cantrowf").val(), like:$('#likef').val(), orden:$("#ordenf").val(), buscar:$("#buscarporf").val()},
			dataType: 'json',
			success: function (data) {
				if(data.length>0){
					var numrow = data[0]['NUM_ROWS'] /30;
					$('#pageselectorf').html('');
					numfil = 0;
					for (var i = 1; i < numrow+1; i++) {
						if(i==1){
							$('#pageselectorf').html($('#pageselectorf').html()+'<option selected="selected" style="font-weight: bold" value="0">'+i+'</option>');
						}else{
							numfil = numfil+30;
							$('#pageselectorf').html($('#pageselectorf').html()+'<option value="'+numfil+'">'+i+'</option>');
						}
					}
					cobros = data;
					saveDetalleF();
				}else{
					$('#detalle-cobros').html('');
					$('#pageselectorf').html('');
				}
			}
		});
	}
});

$(document).on('click', '#closef', function () {

	$('.panel-fac').hide();
});


$(document).on('click', '.regfac', function(){
	var num_cob;
	num_cob = $(this).attr('data-id');
	buscarreg(num_cob, 'DESC');
	$('.panel-fac').hide();
});


function saveDetalleF() {
	renderTableF('detalle-cobros');
}

function renderTableF(data){
	var template = Handlebars.compile( $('#CobrosMasterTemplate').html() );
	$('#'+data).html( template(this) );
}

function cambiotamano(){
	h = $(window).height();
	mg = 50;
	mhc = 450;
	hbp = 0;
	if($(window).height() <= 400){
		$('div.btn-group').hide('fast');
	}else{
		$('div.btn-group').show();
	}
	$('.msgimp .opciones').css({"margin-top":($(window).height()-102)/3+"px"});
	if($(window).width() <= 1050){
	 	$('.conten').css({"width":"100%"});
		$('.tabl').css({"width":"100%", "height":$(window).height()-50});
	}else{
		$('.conten').css({"width":"100%"});
		$('.tabl').css({"width":"100%", "height":$(window).height()-50});
	}
	$('.cuotas').css({"height":$(window).height()-430});
    setTimeout(function() {
    	if($(window).width() <= 1050){
			$('.flexme12').parents('.flexigrid').css({"width":$('.conten').width()-10});
			
		}else{
			$('.flexme12').parents('.flexigrid').css({"width":$('.conten').width()-20});
			$('.bDiv').css({"height":"200px"});
		}
	}, 1000);
	

	if($(window).width() <= 840){
		if(!$("aside#nav").hasClass("nav-vertical")){
			$("aside#nav").addClass("nav-vertical");
		}
	}else{
		$("aside#nav").removeClass("nav-vertical");
	}
}

$(window).resize(function() {
    if(this.resizeTO) clearTimeout(this.resizeTO);
    this.resizeTO = setTimeout(function() {
        $(this).trigger('resizeEnd');
    }, 200);
});

$(window).bind('resizeEnd', function() {
    //do something, window hasn't changed size in 500ms
	cambiotamano();
	


});
$('.msgimp').hide();
$(document).ready(function () {


/*db.transaction(function (tx) {
   tx.executeSql('CREATE TABLE IF NOT EXISTS LOGS (id unique, log)');
   tx.executeSql('INSERT INTO LOGS (id, log) VALUES (1, "foobar")');
   tx.executeSql('INSERT INTO LOGS (id, log) VALUES (2, "logmsg")');
});

db.transaction(function (tx) {
   tx.executeSql('ALTER TABLE dat_cli ADD COLUMN telefono ');
});

db.transaction(function (tx) {
   tx.executeSql('UPDATE dat_cli SET telefono="829-815-6619" WHERE id=1 ');
});
/*db.transaction(function (tx) {
   tx.executeSql('SELECT * FROM dat_cli', [], function (tx, registros) {
      var rows = registros.rows;
      
	
      for (i = 0; i < rows.length; i++){
         alert(rows[i]['nombre'] );
      }
	
   }, null);
});*/

	if(!window.openDatabase){
		//alert("no funciona websql");		
	}
	$('.msgimp .opciones').css({"margin-top":($(window).height()-102)/3+"px"});
	cambiotamano();
	

	$('#div_carga').hide().ajaxStart(function() {
	  	if($(this).hasClass('cargar')){
	  		$(this).show();
	  	}	    
	}).ajaxStop(function() {
	    $(this).hide();
	});
	$('.logo2').hide();
	

/*(function () {
var textFile = null,
  makeTextFile = function (text) {
    var data = new Blob([text], {type: 'text/plain'});

    // If we are replacing a previously generated file we need to
    // manually revoke the object URL to avoid memory leaks.
    if (textFile !== null) {
      window.URL.revokeObjectURL(textFile);
    }

    textFile = window.URL.createObjectURL(data);

    return textFile;
  };


  var create = document.getElementById('btn-fin');
   var textbox = "alexander lopez";

  create.addEventListener('click', function () {
    var link = document.getElementById('desca');
    link.href = makeTextFile(textbox);
    //link.style.display = 'block';
    
    link.download = "info.txt";
	document.getElementById("desca").click();
  }, false);
})();*/



	
});
