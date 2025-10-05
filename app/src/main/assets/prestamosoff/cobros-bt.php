<?php session_start();

include "include/db.php";


header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT");
header("Allow: GET, POST, OPTIONS, PUT");

$datas  = array();


$sql = "SELECT
    *
FROM
    dat_cob where fec_cob >= DATE_SUB( NOW( ) , INTERVAL 10 DAY )  ";




$datas= buscar_db($sql);

foreach ($datas as $p => $value) {
	$diasv = 0;
	$mora = 0;
	if($datas[$p]['mor_cob']=="1"){
		$mora = (2)/100;
	}
	
	$sqla ="SELECT 
				*
			FROM
				dat_dcob
			WHERE 
				num_cob='".$datas[$p]['num_cob']."'  ";
	$cuota= buscar_db($sqla);
	if($tipo=="mobil"){
		$datas[$p]['num_cob'] =  str_pad($datas[$p]['num_cob'], 7, "0", STR_PAD_LEFT);
	}else{
		$datas[$p]['num_cob'] =  str_pad($datas[$p]['num_cob'], 7, "0", STR_PAD_LEFT);
	}			
	
	$datas[$p]['cuota'] =$cuota;

	foreach ($datas[$p]['cuota'] as $i => $value) {
		$sqla ="SELECT 
				*
			FROM
				dat_dpre
			WHERE 
				cuo_cuo='".$datas[$p]['cuota'][$i]['cuo_cuo']."' and num_pre='".$datas[$p]['cuota'][$i]['num_pre']."'  ";
		$cuotas= buscar_db($sqla);

		$datas[$p]['cuota'][$i]['cuo_cuo'] = $cuotas[0]['cuo_cuo'];
		$datas[$p]['cuota'][$i]['fec_cuo']=$cuotas[0]['fec_cuo'];
		
		
		$fecha2 = date("d/m/y", strtotime($cuotas[0]['fec_cuo']));
		$datas[$p]['cuota'][$i]['fec_cuo']=$fecha2;
			
		
		
		$datas[$p]['cuota'][$i]['mon_cuo']=$cuotas[0]['mon_cuo'];

		$diasv = (strtotime($datas[$p]['fec_cob'])-strtotime($cuotas[0]['fec_cuo']))/(60 * 60 * 24);
		
		if($diasv<0){
			$diasv = 0;
		}
		
		//$datas[$p]['cuota'][$i]['mor_cuo']=$datas[$p]['cuota'][$i]['mor_cob'];
		$datas[$p]['cuota'][$i]['pen_cuo']=$datas[$p]['cuota'][$i]['bal_cuo']+$datas[$p]['cuota'][$i]['mor_cob'];
		$datas[$p]['cuota'][$i]['pen_cuo']=number_format($datas[$p]['cuota'][$i]['pen_cuo'], 2, '.', '');
	
		unset($datas[$p]['cuota'][$i]['cod_cuo'], $datas[$p]['cuota'][$i]['fec2_cuo'], $datas[$p]['cuota'][$i]['mon_cuo']);

	}
	
}


echo json_encode($datas);


?>