<?php session_start();

include "include/db.php";


header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT");
header("Allow: GET, POST, OPTIONS, PUT");

$datas  = array();
$data = array();
$ncf = array();
$cli = array();

$subtotal=0;
$mora =0;
$total=0;
$fec = date("Y-m-d");

if( isset($_POST['cobro']) ){

$sql = "SELECT	
			*
		FROM
			dat_cli WHERE cod_cli='".$_POST['cobro']['cli_cob']."' ";
$cli= buscar_db($sql);

$detalle['ref_cob']="CL".$_POST['cobro']['num_cob'];
$detalle['hor_cob']=$_POST['cobro']['hor_cob'];
$detalle['fec_cob']=$_POST['cobro']['fec_cob'];
$detalle['usu_cob']=$_POST['cobro']['usu_cob'];
$detalle['pre_cob']=$_POST['cobro']['pre_cob'];
$detalle['con_cob']=$_POST['cobro']['con_cob'];
$detalle['cli_cob']=$_POST['cobro']['cli_cob'];
$detalle['cobr_cob']=$_POST['cobro']['cobr_cob'];
$detalle['sts_cob']="VALIDO";
$detalle['pos_cob']="N";
$detalle['des_cob']=0.00;
$detalle['fpag_cob']="1";
$detalle['gra_cob']=$_POST['cobro']['gra_cob'];
$detalle['mor_cob']=$_POST['cobro']['mor_cob'];
$detalle['not_cob']="";
$detalle['npag_cob']="";
$detalle['bpag_cob']="";

if( isset($_POST['cobro']['cuota']) ){
	//loop through detalles and handle errors
	foreach($_POST['cobro']['cuota'] as $index => $value ){
		$subtotal+=$value['sub_tot'];
		$mora +=$value['mor_cob'];
		$total+=$value['tot_cob'];
	}
}

$detalle['sub_tot']=$subtotal;
$detalle['mora_cob']=$mora;
$detalle['tot_cob']=$total;

//write factura to table
$insert_visita = db_write('dat_cob', $detalle);


//handle errors
if(!$insert_visita['query']){
	die($data['query']);
} else {

	//log query, results in response
	$data['cobro'] = $insert_visita;

	//get pedido id for detalles in case this is a new entry.
	$data['last_id'] = mysqli_insert_id($link);
	$data['last_id'] = str_pad($data['last_id'], 7, "0", STR_PAD_LEFT);
	$detalle['num_cob'] = mysqli_insert_id($link);		
}


$balcli = $detalle['sub_tot'];
$codcli = $detalle['cli_cob'];
$sqlc="UPDATE dat_cli SET bal_cli = bal_cli-$balcli where cod_cli = '$codcli' ";
$sqlc = query_db($sqlc);

if( isset($_POST['cobro']['cuota']) ){
	//loop through detalles and handle errors
	foreach($_POST['cobro']['cuota'] as $index => $value ){
		//set pedido id
		$value['num_cob'] = $data['last_id'];

		unset($value['pen_cuo'], $value['fec_cuo']);
		
		//run the insert
		$insert_detalle = db_write('dat_dcob', $value );
			
		//handle errors
		if(!$insert_detalle['query']){
			die($insert_detalle['query']);
		} else {
			$data['detalles'][$index] = $insert_detalle; //log query, results in response
			$balfac = $value['sub_tot'];
			$balsub = $value['sub_tot'];
			$cuocuo = $value['cuo_cuo'];
			$num_pre = $value['num_pre'];
			
			$sqlc="UPDATE dat_pre SET bal_pre = bal_pre-$balfac where num_pre = '$num_pre' ";
			$sqlc = query_db($sqlc);

			$sqlc="UPDATE dat_dpre SET bal_cuo = bal_cuo-$balsub where num_pre = '$num_pre' and cuo_cuo = '$cuocuo' ";
			$sqlc = query_db($sqlc);

			if($value['mor_cob']>0){
				/*$balcli = $detalle['mor_cob'];
				$codcli = $_POST['detalle']['cli_cob'];
				$sqlc="UPDATE dat_cli SET bal_cli = bal_cli+$balcli where cod_cli = '$codcli' ";
				$sqlc = query_db($sqlc);

				$balfac = $detalle['mor_cob'];
				$numfac = $detalle['num_pre'];
				$sqlc="UPDATE dat_pre SET bal_pre = bal_pre+$balfac where num_pre = '$numfac' ";
				$sqlc = query_db($sqlc);*/				
			}
					
		}
	}
}

echo json_encode($data);


}

?>