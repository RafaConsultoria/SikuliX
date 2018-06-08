package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import model.Settings;
import model.UserAmdocs;

public class CreateContractWindow extends PrimalWindow{
	
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	private enum ContractActions{
		VALIDARSIMONLY("Dado un usuario de tipo presencial se valida la SIM y se lanza la orden"),
		VALIDARSIMONLY_PORTABILIDAD_MOVIL("Dado un usuario de tipo preencial se valida una SIM de portabilidad, se rellena el formulario y se lanza la orden"),
		VALIDAR_SIM_IMEI_TIENDA("Dado un usuario de tipo presencial se valida el IMEI y la SIM, se compra un terminal en tienda y se valida la orden"),
		VALIDAR_IMEI_TIENDA("Dado un usuario de tipo presencial se valida el IMEI, se compra un terminal en tienda y se valida la orden");
		
		private final String text;
	
		 /**
		  * @param text
		  */
		private ContractActions(final String text) {
		    this.text = text;
		}
	
		 /* (non-Javadoc)
		  * @see java.lang.Enum#toString()
		  */
		@Override
		public String toString() {
		    return text;
		}
	};
	/********************************************************************************************************
	 *  			FUNCIONES DE APOYO
	 ********************************************************************************************************/
	/**
	 * se lleva a cabo la l�gica de firma
	 */
	private void signatureAcion() {
		Location loc;
		try {
			loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "signature_menu.PNG") ).getTarget();
			getMyScreen().click(loc);
			selectFromMenuInputDown(1);
			loc.x+=70;
			waitInSecs(5);
			getMyScreen().click(loc);
		} catch (FindFailed e) {
			e.printStackTrace();
		}
	}
	/**
	 * se pulsa el scoring, cerrando popUps y esperando su carga
	 */
	private void scoringAction() {
		try {
			getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "scoring_button.PNG") ).click();
			waitAndCloseMessagePopUp(1);
			waitInSecs(12);
		} catch (FindFailed e) {
			e.printStackTrace();
		}
	}
	/**
	 * se pulsa el boton de continuar finalizar y se esperan los popups
	 * @throws FindFailed 
	 */
	private void continuar_finalizarAction() throws FindFailed {
		getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "continuar_finalizar_button.PNG") ).click();
		waitAndCloseMessagePopUp(2);
		waitInSecs(2);
	}
	/**
	 * Funci�n que abarca toda la l�gica que se ejecute en la tienda
	 * @return
	 */
	private boolean StoreProcess() {
		boolean exit = false;
		try {
		if(WaitFor("Esperando a que se carge la tienda",
				new HashMap<Pattern, Boolean>() {
			private static final long serialVersionUID = 1L;
			{
				put(new Pattern(reescaledImage(getWindowPath()+"store/","tittle_window_store.PNG")).similar(0.9f),true);
				put(new Pattern(reescaledImage(getWindowPath()+"store/","reference_of_load_phase1.PNG")).similar(0.9f),true);
				
			}},15)) {
				screenShot("metodo_pago");
				getMyScreen().find( reescaledImage(getWindowPath()+"store/", "pay_method_cash.PNG") ).click();
			
			//barra de carga para esperar a los detalles de compra
			if(WaitFor("Esperando a que se carguen los detalles de la compra",
					new HashMap<Pattern, Boolean>() {
				private static final long serialVersionUID = 1L;
				{
					put(new Pattern(reescaledImage(getWindowPath()+"store/","reference_of_load_phase2.PNG")).similar(0.9f),true);
					
				}},15)) {
					screenShot("compra_confirmada");
					getMyScreen().find( reescaledImage(getWindowPath()+"store/", "confirmar_compra_button.PNG") ).click();
					//barra de carga para esperar a la confirmaci�n de la compra
					if(WaitFor("Esperando a que se carguen los detalles de la compra",
							new HashMap<Pattern, Boolean>() {
						private static final long serialVersionUID = 1L;
						{
							put(new Pattern(reescaledImage(getWindowPath()+"store/","reference_of_load_phase3.PNG")).similar(0.9f),true);
							
						}},15)) {
						screenShot("finalizar_compra");
						getMyScreen().find( reescaledImage(getWindowPath()+"store/", "close_store_button.PNG") ).click();
						
						exit = true;
					}
				}
			}
		} catch (FindFailed e) {
			e.printStackTrace();
		}
		return exit;
	}
	/**
	 * Script de validaci�n de un IMEI
	 * @param IMEI
	 */
	private void validate_IMEI(String IMEI) {
		// Busca la fila para a�adir IMEI dejando vac�a primero la casilla
		Location loc;
		try {
			loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "add_imei_row.PNG") ).getTarget();
			getMyScreen().doubleClick(loc);
			for(int i = 0;i<10;i++) {
				getMyScreen().type(Key.BACKSPACE);
				waitInMilisecs(300);
			}
			getMyScreen().paste(IMEI);
			// Mueve a la derecha la localizaci�n para hacer click en agregar IMEI y cierra los PopUps generados
			loc.x+=100;
			waitInSecs(3);
			getMyScreen().click(loc);
			waitAndCloseMessagePopUp(2);
		} catch (FindFailed e) {
			e.printStackTrace();
		}
	}
	/**
	 * Script de valicaci�n de una SIM
	 * @param SIM
	 */
	private void validate_SIM(String SIM) {
		Location loc;
		// Busca la fila para a�adir SIM dejando vac�a primero la casilla
	    try {
			loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "add_sim_row.PNG") ).getTarget();
			getMyScreen().doubleClick(loc);
			for(int i = 0;i<10;i++) {
				getMyScreen().type(Key.BACKSPACE);
				waitInMilisecs(300);
			}
			getMyScreen().paste(SIM);
			// Mueve a la derecha la localizaci�n para hacer click en agregar SIM y cierra los PopUps generados
			loc.x+=100;
			waitInSecs(3);
			getMyScreen().click(loc);
			waitAndCloseMessagePopUp(2);
	    } catch (FindFailed e) {
			e.printStackTrace();
		}
	}
/********************************************************************************************************
 *  			FUNCIONES PRINCIPALES
 ********************************************************************************************************/
	
	
	/**
	 * Funci�n script para tramitar la orden de <i>SIM con terminal</i> en la ventana de creaci�n de contratos. <br><br>
	 * pasos a realizar
	 * <ul>
	 * <li>validar numero IMEI</li>
	 * <li>validar numero SIM<br>
	 * <li>pulsar boton acceso tienda</li>
	 * <li>esperar a que se abra la tienda</li>
	 * <li>seleccionar metodo de pago, en este caso al contado</li>
	 * <li>confirmamos la compra</li>
	 * <li>vemos que se ha realizado bien y cerramos la ventana</li>
	 * <li>pulsamos el boton de scoring cerrando los popups habituales</li>
	 * <li>pulsamos el boton de continuar finalizar + sus popups</li>
	 * <li>introducimos la firma manual</li>
	 * <li>volvemos a pulsar el boton de continuar finalizar y sus popups</li>
	 * </ul>
	 * @param tmp
	 * @return
	 */
	private boolean validar_SIM_IMEI_ConTiendaEVA(Queue<String> tmp) {
		boolean exit = false;
		String IMEI = tmp.poll();
		IMEI = IMEI.split("\\$")[1];
		String SIM = tmp.poll();
		SIM = SIM.split("\\$")[1];
		waitInSecs(15);
		try {
			validate_IMEI(IMEI);
			validate_SIM(SIM);
			mover_ventana("ABAJO");
			getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "acceso_tienda_button.PNG") ).click();
			if(StoreProcess()) {
				scoringAction();
				continuar_finalizarAction();
				CheckLoadBar();
				waitInSecs(5);
				signatureAcion();
				waitAndClosePDF(1);
				//se finaliza la orden
				continuar_finalizarAction();			
				exit = true;
				CheckLoadBar();//para estabilizar el programa antes de cerrar las ventanas
			}		
		} catch (FindFailed e) {
			
			System.out.println("no se ha encontrado una imagen en creaci�n de contrato");
			e.printStackTrace();
		}
		return exit;
	}


	/**
	 * Funci�n script para tramitar la orden de <i>IMEI con terminal</i> en la ventana de creaci�n de contratos. <br><br>
	 * pasos a realizar
	 * <ul>
	 * <li>validar numero IMEI</li>
	 * <li>pulsar boton acceso tienda</li>
	 * <li>esperar a que se abra la tienda</li>
	 * <li>seleccionar metodo de pago, en este caso al contado</li>
	 * <li>confirmamos la compra</li>
	 * <li>vemos que se ha realizado bien y cerramos la ventana</li>
	 * <li>pulsamos el boton de scoring cerrando los popups habituales</li>
	 * <li>pulsamos el boton de continuar finalizar + sus popups</li>
	 * <li>introducimos la firma manual</li>
	 * <li>volvemos a pulsar el boton de continuar finalizar y sus popups</li>
	 * </ul>
	 * @param tmp
	 * @return
	 */
	private boolean validar_IMEI_ConTiendaEVA(Queue<String> tmp) {
		boolean exit = false;
		String IMEI = tmp.poll();
		IMEI = IMEI.split("\\$")[1];
		waitInSecs(15);
		try {
			validate_IMEI(IMEI);
			// Bajamos con el scroll de la ventana y accedemos a la tienda
			mover_ventana("ABAJO");
			getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "acceso_tienda_button.PNG") ).click();
			if(StoreProcess()) {
				scoringAction();
				continuar_finalizarAction();
				CheckLoadBar();
				waitInSecs(5);
				signatureAcion();
				waitAndClosePDF(1);
				//se finaliza la orden
				continuar_finalizarAction();			
				exit = true;
				CheckLoadBar();//para estabilizar el programa antes de cerrar las ventanas
			}		
		} catch (FindFailed e) {
			
			System.out.println("no se ha encontrado una imagen en creaci�n de contrato");
			e.printStackTrace();
		}
		return exit;
	}
	/**
	 * Funci�n script para tramitar la orden de <i>SIM only</i> en la ventana de creaci�n de contratos.
	 * @param tmp
	 * @return
	 */
	private boolean validarSIMOnly(Queue<String> tmp) {
		boolean exit = false;
		try {
			String SIM = tmp.poll();
			SIM = SIM.split("\\$")[1];
			waitInSecs(15);
			validate_SIM(SIM);
			mover_ventana("ABAJO");
			scoringAction();
			continuar_finalizarAction();
			CheckLoadBar();
			waitInSecs(5);
			signatureAcion();
			waitAndClosePDF(1);
			continuar_finalizarAction();				
			exit = true;
		} catch (FindFailed e) {
			e.printStackTrace();
		}
		return exit;
	}
	/**
	 * Funci�n script para tramitar la validaci�n de una orden SIMonly con portabilidad. <br>
	 * <ul>
	 * <li> validar numero SIM de portabilidad </li>
	 * <li> accedemos al tab de portabilidad movil </li>
	 * <li> rellenamos el formulario correctamente </li>
	 * <li> guardamos el formulario rellenado </li>
	 * <li> pulsamos el boton de scoring cerrando los popups habituales </li>
	 * <li> pulsamos el boton de continuar finalizar + sus popups </li>
	 * <li> introducimos la firma manual </li>
	 * <li> volvemos a pulsar el boton de continuar finalizar y sus popups </li>
	 * </ul>
	 * 
	 * @param tmp
	 * @return
	 */
	private boolean validarSIMOnly_portabilidad_movil(Queue<String> tmp) {
		boolean exit = false;
		try {
			String SIM = tmp.poll();
			SIM = SIM.split("\\$")[1];
			waitInSecs(15);
			validate_SIM(SIM);
			// TODO falta la interaacion con  el formulario de portabilidad movil
			scoringAction();
			continuar_finalizarAction();
			CheckLoadBar();
			waitInSecs(5);
			signatureAcion();
			waitAndClosePDF(1);
			continuar_finalizarAction();	
		} catch (FindFailed e) {
			e.printStackTrace();
		}
		return exit;
	}
	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	public CreateContractWindow(UserAmdocs userAmdocs, Settings settings) {
		super(userAmdocs,settings);
		setWindowPath(getRepoPath()+"windows/createContract/");
	}
	public CreateContractWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			String MyrepoPath, String sourceAction,Queue<Pattern> nextWindowScript,Settings settings) {
		super(myScreen, references, data, userLogged, settings, sourceAction, sourceAction, sourceAction, nextWindowScript);
		setWindowPath(getRepoPath()+"windows/createContract/");
	}
	/**
	 * Dada una accion selecciona la ejecucion apropiada
	 * @param tmp
	 * @return
	 * @throws Exception 
	 */
	public boolean start(Queue<String> tmp) throws Exception {
		boolean correct = false;
		// se espera a la carga de la ventana
		if(
				CheckLoadBar() && 
				WaitFor("Esperando a que se cargue el t�tulo de la ventana de creaci�n de contrato",
				new HashMap<Pattern, Boolean>() {/**
				* busca si esta el boton de cerrar ventana
				*/
				private static final long serialVersionUID = 1L;
				{
					put(new Pattern(reescaledImage("windowTittle.PNG")).similar(0.9f),true);
				}},15) 
		) {
			String ta = tmp.poll();//tipo de acci�n	
			System.out.println("Descripci�n de la acci�n:");
			switch(ContractActions.valueOf(ta.toUpperCase())) {
				case VALIDARSIMONLY:
					setSourceAction("VALIDARSIMONLY");
					System.out.println("se introduce una SIM y se valida");
					System.out.println("---------------------------------");	
					correct = validarSIMOnly(tmp);
					break;
				case VALIDAR_SIM_IMEI_TIENDA:
					setSourceAction("VALIDAR_SIM_IMEI_TIENDA");
					System.out.println("se compra una terminal y se validan los datos");
					System.out.println("---------------------------------");	
					correct = validar_SIM_IMEI_ConTiendaEVA(tmp);
					break;
				case VALIDAR_IMEI_TIENDA:
					setSourceAction("VALIDAR_IMEI_TIENDA");
					System.out.println("se compra una terminal y se validan los datos");
					System.out.println("---------------------------------");	
					correct = validar_IMEI_ConTiendaEVA(tmp);
					break;
				case VALIDARSIMONLY_PORTABILIDAD_MOVIL:
					setSourceAction("VALIDARSIMONLY_PORTABILIDAD_MOVIL");
					System.out.println("se introduce una SIM de portabilidad, se validan los datos y se rellena el formulario de portabilidad");
					System.out.println("---------------------------------");
					correct = validarSIMOnly_portabilidad_movil(tmp);
					break;
				default: 
					System.out.println("Acci�n no contemplada");
					correct = true;	
			}
		}
		return correct;													  	}

	
}
