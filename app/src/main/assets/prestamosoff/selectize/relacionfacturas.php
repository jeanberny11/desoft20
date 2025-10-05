<?php 
include "../modulos/theme1.php";

$fecha = date("Y-m-d");
$fecha1 = date("Y-m-d");
if(isset($_GET['theDate'])){
	$fecha = $_GET['theDate'];
	$fecha1 = $_GET['theDate1'];
}
if(isset($_GET['theDate'])){
	$fecha = $_GET['theDate'];
	$fecha1 = $_GET['theDate1'];
}

$html2 = '<option value="EN MANO">EN MANO</option><option value="PAGADA">PAGADA</option><option value="COLEGA">COLEGA</option><option value="CLIENTE">CLIENTE</option><option value="NO EN MANO">NO EN MANO</option>';
?>

<html>
<head>

<script type="text/javascript" src="../js/jquery.js" mce_src="../js/jquery.js"></script>
<link rel="stylesheet" type="text/css" href="../css/flexigrid.css" >
<link rel="stylesheet" type="text/css" href="../css/flexigrid.pack.css">
<script type="text/javascript" src="../js/flexigrid.js"></script>
<script type="text/javascript" src="../js/flexigrid.pack.js"></script>
<script type="text/javascript" src="../js/selectize.js"></script>
<link rel="stylesheet" href="../selectize/dist/css/selectize.bootstrap3.css">
<script type="text/javascript" src="../js/select.js"></script>
<script type="text/javascript" src="../js/handlebars.js" mce_src="../js/handlebars.js"></script>
<script type="text/javascript" src="../js/undercore.js" mce_src="../js/undercore.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<script type="text/javascript">

(function ($) {
	$.fn.selectizeThis = function (options) {
		var self = this;
		var select = this.selectize(options);
		var selectInstance = select[0].selectize;
		
		var selectClickHandler = function () {
			this.blur();
			$('body').scrollTop(10);
		};

		var selectFocus = function(){
			selectInstance.clear();
		}

		//this.siblings('.selectize-control').find('selectize-input').addClass('selectize-input');

		selectInstance.on('item_add', selectClickHandler);
		selectInstance.on('dropdown_open', selectFocus);

		return this;
	};
})(jQuery);


function buscar_op(obj, cadena){
    var puntero=cadena.length;
    var encontrado=false;
    var opcombo=0;
	while (!encontrado && (opcombo < document.getElementById(obj).length)){
        if(document.getElementById(obj)[opcombo].value.substr(0,puntero).toLowerCase()==cadena.toLowerCase()){
            document.getElementById(obj).selectedIndex=opcombo;
        	encontrado=true;
        }else{
        	opcombo++;
    	}
	}
}
function buscar_opp(obj, cadena){
    var puntero=cadena.length;
    var encontrado=false;
    var opcombo=0;
	while (!encontrado && (opcombo < document.getElementById(obj).length)){
        if(document.getElementById(obj)[opcombo].text.substr(0,puntero).toLowerCase()==cadena.toLowerCase()){
            document.getElementById(obj).selectedIndex=opcombo;
        	encontrado=true;
        }else{
        	opcombo++;
    	}
	}
}

var bloqueo = 'N';

</script>
<style type="text/css">
	#thedate{
		max-width: 10em;
	}
	
</style>
<title></title>

</head>
<body>
<div id="div_carga">
<img id="cargador" src="../images/ajax-loader.gif"/>
</div>
<div class="conten">
	<div class="tabl">
<fieldset>
<legend align="center" margin-bottom="0px"><font size="5">RELACION FACTURAS ENTREGADAS</font></legend>
<form action="" method="get" name="pac" id="pac">
<div class="checkbox">
    <label>
      <input type="checkbox" id="full-screen"> Pantalla Completa
    </label>
</div>
<table class="tbl">
	<tr>
		<td>Condicion:</td>
		<td>
		<div class="form-group input-group-sm">
			<select class="form-control" name="tipo" id="tipo">
				<option value="entregadas">ENTREGADAS</option>
				<option value="noentregadas">NO ENTREGADAS</option>
			</select>
		</div>
		</td>
	</tr>
</table>
<div class="vendedor-select-col">
    <div class="sel-wrap">
        <select id="vendedor-sel" class="selectize" placeholder="Seleccionar Vendedor">
        	
        </select>                    
    </div>
</div>

				
			<?php 
			$htmlp= '';
			$ven=mysqli_query($link, "select * from dat_ven ");
				while($venr=mysqli_fetch_array($ven)){
					if($_SESSION['tipog']!="VENDEDOR"){
						$htmlp =$htmlp.'<option value="'.$venr['COD_VEN'].'">'.$venr['NOM_VEN'].'</option>';
					}else{
						if($_SESSION['codv']==$venr['COD_VEN']){
							$htmlp =$htmlp.'<option value="'.$venr['COD_VEN'].'">'.$venr['NOM_VEN'].'</option>';
						}
					}
			 } ?>
		

 <table width="100%">
<tr>




<td style="max-width: 50px;">
<div class="butt">
<button class="btn btn-md btn-info procesar-pedido butt" id="procesar-f"><span class="glyphicon glyphicon glyphicon glyphicon-share"></span>&nbsp;&nbsp;Procesar</button>
</div>
</td>
<td style="max-width: 40px;">
<div class="butt">
<button class="btn btn-md btn-info procesar-pedido" id="export">
<span class="glyphicon glyphicon glyphicon glyphicon-export"></span>&nbsp;&nbsp;Excel</button>
</div>
</td>
<td >
<div class="butt">
<button class="btn btn-md btn-success butt" id="abrir-r"><span class="glyphicon glyphicon glyphicon glyphicon-edit"></span>&nbsp;&nbsp;Modo Edicion</button>
</div>
</td>

<td align="right">
<div class="butt">
<button class="btn btn-md btn-info butt" id="procesar-r"><span class="glyphicon glyphicon glyphicon glyphicon-share"></span>&nbsp;&nbsp;Procesar Relacion</button>
</div>
</td>
</tr>
 </table>

<table class="flexme12 table-striped">
<thead>
<tr>
<th width="80">No. Fact</th>
<th width="80">Fecha</th>
<th width="60">Plazo</th>
<th width="80">F. Vencimiento</th>
<th width="80">Dias Venc.</th>
<th width="80">Dias Faltantes</th>
<th width="55">Cod Cli</th>
<th width="200">Cliente</th>
<th width="90">Monto</th>
<th width="90">Balance</th>
<th width="150">Vendedor</th>
<th width="210">Entrega A.</th>
<th width="180">Fecha Ent.</th>
<th width="210">Status</th>
<th width="180">Fecha Arq.</th>
<th width="400">Comentario</th>
</tr>
</thead>
<tbody>

</tbody>
</table>
</form>
<script type="text/javascript">
	$('.flexme12').flexigrid({'height': 250});
var op = [];
var detalles = [];
var pp ="";
var pl = "";
var pt = "";
$('#procesar-f').click(function () {
	event.preventDefault();
	$.ajax({
		url: 'relacionfacturas-p.php',
		type: 'post',
		data: {CONDICION: $('#tipo').val(), VEN: $('#vendedor-sel').val()},
		dataType: 'html',
		success: function (data) {
			//console.log(data);
			$('.flexme12 tbody').html(data);
			$('[data-id]').html('<option value=""></option><?php echo $htmlp; ?>');
			$('[data-idss]').html('<option value=""></option><?php echo $html2; ?>');
			op = $('[data-id]');

		}
	});
});

$('#export').click(function () {
	event.preventDefault();

	$.ajax({
		url: 'relacionfacturae.php',
		type: 'post',
		data: {CONDICION: $('#tipo').val(), VEN: $('#vendedor-sel').val()},
		dataType: 'html',
		success: function (data) {
			//console.log('asdas');
			//$('#factura').click();
			window.location.href = 'relacionfacturae.xlsx';
		}
	});
});

$('#procesar-r').click(function () {
	event.preventDefault();
	
	for (var i = 0; i < op.length; i++) {
		if($(op[i]).val()!=''){
			var detalle = {};
			detalle.NUM_FAC=$(op[i]).attr('data-id');
			detalle.NOM_VENF = $(op[i]).val();
			detalle.FEC_ENT = $('[data-idf='+detalle.NUM_FAC+']').val();
			detalle.STS_FAC = $('[data-idss='+detalle.NUM_FAC+']').val();
			detalle.FEC_ARQ = $('[data-idfa='+detalle.NUM_FAC+']').val();
			detalle.COMENT = $('[data-idca='+detalle.NUM_FAC+']').val();
			detalles.push(detalle);
		}

	}
	
	$.ajax({
		url: 'guardarrelacionfacturas.php',
		type: 'post',
		data: {detalle: detalles},
		dataType: 'html',
		success: function (data) {
			//console.log(data);
			$.ajax({
				url: 'relacionfacturas-p.php',
				type: 'post',
				data: {CONDICION: $('#tipo').val(), VEN: $('#vendedor-sel').val()},
				dataType: 'html',
				success: function (data) {
					//console.log(data);
					$('.flexme12 tbody').html(data);
					$('[data-id]').html('<option value=""></option><?php echo $htmlp; ?>');
					$('[data-idss]').html('<option value=""></option><?php echo $html2; ?>');
					op = $('[data-id]');
					
				}
			});
		}
	});
});

	

$('#div_carga')
			.hide()
			.ajaxStart(function() {
				$(this).show();
			})
			.ajaxStop(function() {
				$(this).hide();
			});

$('#COD_VEN').html('<option value="TODOS">TODOS</option><?php echo $htmlp; ?>');


$('#abrir-r').click(function () {
	event.preventDefault();
	var rt;
	var rn;
	var ps;
	rt = $('[data-idc]');

	rn = rt.length;
	$('#div_carga').show();
	ps = 1;
	console.log('empeso');
	for (var i = 0; i < rn; i++) {
	
		if(ps >= rn){
			$('#div_carga').hide();
		}
		ps ++;

		ll = rt[i];

		pl = $(ll).attr('data-idc');
		pp = $('[data-idc='+pl+'] div').html();
		busq = '';
		COD_VEN = '';
		NOM_VEN = '';
		busq = _.filter(vendedores, function(detalle){
	  		return detalle.NOM_VEN == pp; 
		});
		
		if(busq.length>0){
			COD_VEN = busq[0]['COD_VEN'];
			NOM_VEN = busq[0]['NOM_VEN'];
			
		

			$(ll).html('<div style="width: 210px;"><select name="entregado" id="entregado" data-id="'+pl+'"></select></div>');
			$('[data-id='+pl+']').html('<option value="'+COD_VEN+'" selected>'+NOM_VEN+'</option><?php echo $htmlp; ?>');
			
			pp = $(ll).next().html();
			pp = pp.replace('<div style="width: 180px;">', '');
			pp = pp.replace('</div>', '');

			
			$(ll).next().html('<div style="width: 180px;"><div class="form-group input-group-sm"><input class="form-control" type="date" name="thedate" id="thedate" value="'+pp+'" data-idf="'+pl+'"/></div></div>');
			
			pp = $('[data-ids='+pl+'] div').html();	
			if(pp.trim()==''){
				pp = 'EN MANO';
			}
			$('[data-ids='+pl+']').html('<div style="width: 210px;"><select name="status" id="status" data-idss="'+pl+'"></select></div></div>')
			$('[data-idss='+pl+']').html('<option value="'+pp+'" selected>'+pp+'</option><?php echo $html2; ?>');
			$('[data-ids='+pl+']').next().html('<div style="width: 180px;"><div class="form-group input-group-sm"><input class="form-control" type="date" name="thedate" id="thedate" value="<?php echo $fecha ?>" data-idfa="'+pl+'"/></div></div>');
			$('[data-idqw='+pl+']').next().html('<div style="width: 400px;"><div class="form-group input-group-sm"><input class="form-control" type="text" name="COMENT" id="COMENT" value="" data-idca="'+pl+'"/></div></div>');
			op = $('[data-id]');

			if(bloqueo == 'S'){
				$('[data-idf='+pl+']').attr('readonly', 'true');
				$('[data-idfa='+pl+']').attr('readonly', 'true');
			}
		}	
	}

});

function cambio(lp){
	//console.log(ll);

	ll = $('[data-idc='+lp+']');

	

	pl = $(ll).attr('data-idc');
	pp = $('[data-idc='+pl+'] div').html();
	busq = '';
	COD_VEN = '';
	NOM_VEN = '';
	console.log(pp);
	busq = _.filter(vendedores, function(detalle){
  		return detalle.NOM_VEN == pp; 
	});
	
	if(busq.length>0){
		COD_VEN = busq[0]['COD_VEN'];
		NOM_VEN = busq[0]['NOM_VEN'];
		
	

		$(ll).html('<div style="width: 210px;"><select name="entregado" id="entregado" data-id="'+pl+'"></select></div>');
		$('[data-id='+pl+']').html('<option value="'+COD_VEN+'" selected>'+NOM_VEN+'</option><?php echo $htmlp; ?>');
		
		pp = $(ll).next().html();
		pp = pp.replace('<div style="width: 180px;">', '');
		pp = pp.replace('</div>', '');

		
		$(ll).next().html('<div class="form-group input-group-sm"><input class="form-control" type="date" name="thedate" id="thedate" value="'+pp+'" data-idf="'+pl+'"/></div>');
		
		pp = $('[data-ids='+pl+'] div').html();	
		if(pp.trim()==''){
			pp = 'EN MANO';
		}
		$('[data-ids='+pl+']').html('<div style="width: 210px;"><select name="status" id="status" data-idss="'+pl+'"></select></div>')
		$('[data-idss='+pl+']').html('<option value="'+pp+'" selected>'+pp+'</option><?php echo $html2; ?>');
		$('[data-ids='+pl+']').next().html('<div class="form-group input-group-sm"><input class="form-control" type="date" name="thedate" id="thedate" value="<?php echo $fecha ?>" data-idfa="'+pl+'"/></div>');
		$('[data-idqw='+pl+']').next().html('<div class="form-group input-group-sm"><input class="form-control" type="text" name="COMENT" id="COMENT" value="" data-idca="'+pl+'"/></div>');
		op = $('[data-id]');

		if(bloqueo == 'S'){
			$('[data-idf='+pl+']').attr('readonly', 'true');
			$('[data-idfa='+pl+']').attr('readonly', 'true');
		}
	}

}




$('#full-screen').click(function () {
	
	 if($(this).is(':checked')) {  
	 	 $('.conten').css("width", "98%");
		$('.tabl').css({"width":"98%", "max-width":"2000px"});
		var alt = $(window).height()-350;
		$('.bDiv').css("height", alt+"px");
          
    } else {  
       
		$('.conten').css("width", "70%");
		$('.tabl').css({"width":"90%", "max-width":"750px"});
		var alt = $(window).height()-350;
		$('.bDiv').css("height", "250px"); 
    }  
	
});
 $('.conten').css("width", "98%");
		$('.tabl').css({"width":"98%", "max-width":"2000px"});
		var alt = $(window).height()-350;
		$('.bDiv').css("height", alt+"px");
$("#full-screen:checkbox").attr('checked', true);


$(document).ready(function () {
	var step1, step2;

	selectizeVendedor = {};
	step1 = $.getJSON('vendedorsel.php', function(data) {
			vendedores = data;
			selectizeVendedor = $('#vendedor-sel').selectizeThis({
				maxItems   : 1,
				valueField : 'COD_VEN',
				labelField : 'NOM_VEN',
				searchField: 'SEAR_FIELD',
				options    : vendedores,
				create     : false,
				maxOptions : 10
			});
		});

	step2 = step1.then(
		function (data) {
	    	var def = new $.Deferred();
	    	
	    	$.ajax({
			    url: 'session.php',
			    type: 'post',
			    data: {},
			    dataType: 'json',
			 	success: function (data) {

			 		if(data.status=="ok"){
				    	if(data.msg=="VENDEDOR"){
				    		selectizeVendedor[0].selectize.setValue(data.id);
							selectizeVendedor[0].selectize.disable();
							bloqueo = 'S';
						}else{
							selectizeVendedor[0].selectize.setValue(data.id);
						}
					}else{
						alert('Session No confirmada');
						window.location.reload();
					}
			    	def.resolve();

			    }
			});
		    	
	    	
	    	return def.promise();
	  	},
	  	function (err) {
	        console.log('Step1 failed: Ajax request');
	    }
	);

	step2.done(function () {
		// console.log('dd');

	});
});	


</script>

<br>
</fieldset>
</div>
</div>
</body>
</html>
