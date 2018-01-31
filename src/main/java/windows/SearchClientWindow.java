package windows;

import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import model.UserAmdocs;

public class SearchClientWindow extends PrimalWindow{
	
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	private enum SearchClientActions{
		SELECTCLIENT("seleccionar cliente");
		
		private final String text;
	
		 /**
		  * @param text
		  */
		private SearchClientActions(final String text) {
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
	
	private void openWindow() throws FindFailed, IOException {
		dinamicClick(
				"Buscar",
				"Cambria",
				"#272b2d",
				new int[] {255,255,255},
				Font.PLAIN,
				18,
				true);

		waitInMilisecs(800);
		getMyScreen().type(Key.UP);
		getMyScreen().type(Key.ENTER);
		
		waitInMilisecs(1000);
	}
	private void findClient(Queue<String> tmp) throws Exception {
		Location loc = null;
		final Pattern errorFinderButtons = new Pattern(getRepoPath()+"errorFinderButtons.PNG").similar(0.95f);
		//emula barra de carga para el formulario de busqueda
		if(WaitFor("cargando ventana",
			new HashMap<Pattern, Boolean>() {/**
				 * busca si esta el boton de cerrar ventana
				 */
				private static final long serialVersionUID = 1L;

			{
				put(errorFinderButtons,true);
			}})) {
			loc = getMyScreen().findBest(getRepoPath()+"clearButton.png").getTarget();
			getMyScreen().click(loc);
		}else
			throw new Exception("timeout para errorFinderButtons");//se lanza excepcion por timeout de la espera


		getMyScreen().keyDown(Key.SHIFT);
		//nos situamos en el principio del formulario
		for(int i = 0;i<17;i++)
			getMyScreen().type(Key.TAB);
		getMyScreen().keyUp(Key.SHIFT);
		//descomponemos el string con los campos del formulario de b�squeda
		while(!tmp.isEmpty()) {
			String s = tmp.poll();
			String[] parts = s.split("\\$");
			if(parts.length > 1)
				getMyScreen().paste(parts[1]);
			getMyScreen().type(Key.TAB);
		}
		
		loc = getMyScreen().find(getRepoPath()+"FindButton.png").getTarget();

		getMyScreen().click(loc);
		loc.x+=90;
		getMyScreen().click(loc);//desplazo a la derecha el raton apara quitar la vista hover en el boton de busqueda
		
		//emula barra de carga para la busqueda en la base de datos de un cliente
		final Pattern findButtonLoading = new Pattern(getRepoPath()+"findButtonLoading.png").similar(0.95f);
		final Pattern ClientMenuCheck = new Pattern(getRepoPath()+"ClientMenuCheck.PNG").similar(0.95f);
		
		System.out.print("Buscando en la base de datos");
		if(WaitFor("Buscando en la base de datos",
				new HashMap<Pattern, Boolean>() {/**
					 * busca si esta el boton de cerrar ventana
					 */
					private static final long serialVersionUID = 1L;

				{
					put(ClientMenuCheck,false);
					put(findButtonLoading,true);
				}})) 
		{
				loc = getMyScreen().findBest(getRepoPath()+"clearButton.png").getTarget();
				getMyScreen().click(loc);
		}else
				throw new Exception("timeout para busqueda en la base de datos");//se lanza excepcion por timeout de la espera

	}
	/**
	 * Busca un cliente en el formulario y lo selecciona para pasar a su pagina principal de iteraccion
	 * @param tmp
	 * @return
	 * @throws FindFailed 
	 */
	private boolean selectClient(Queue<String> tmp) throws FindFailed {
		Location loc = null;
		Match m = null;
		boolean exit = false;

		try {
			openWindow();	
			findClient(tmp);
			if(getMyScreen().exists("src/main/resources/images/PopUps/Message.png") != null) {
				screenShot("__NO__ENCONTRADO");
				//en caso de no existir cliente
				waitInMilisecs(1000);
				getMyScreen().type(Key.ENTER);
				System.out.println("\n\n�Cliente no encontrado! fin de la ejecucion\n");
				exit = false;
				
			}else {
				//si existe comprobamos errores inesperados
				waitInMilisecs(1000);
				screenShot("__ENCONTRADO");
				System.out.println("\n\n�Busqueda de cliente realizada correctamente!\n");
				m = getMyScreen().findBest(getRepoPath()+"ClientMenuCheck.PNG");

				final Pattern selectClientButtonOff= new Pattern(getRepoPath()+"selectClientButtonOff.png").similar(0.95f);
				//comprobamos si se ha cargado bien el boton de seleccionar
				do {
					m.below().findBest("images/CheckCircle.PNG").click();
					TimeUnit.MILLISECONDS.sleep(3000);
				} while(getMyScreen().exists(selectClientButtonOff) != null);
				getMyScreen().click(getRepoPath()+"selectClientButton.PNG");
				exit = true;
			}
		} catch (FindFailed e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			exit = false;
			System.err.println(e);
		}finally {
		}
		return exit;
	}
	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	public SearchClientWindow() {
		super();
		setRepoPath(getRepoPath()+"windows/findClient/");
	}
	public SearchClientWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			String MyrepoPath, String sourceAction) {
		super(myScreen, references, data, userLogged, sourceAction);
		setRepoPath(getRepoPath()+"windows/findClient/");
	}
	/**
	 * Dada una accion selecciona la ejecucion apropiada
	 * @param tmp
	 * @return
	 * @throws FindFailed 
	 */
	public boolean start(Queue<String> tmp) throws FindFailed {
		boolean correct = false;
		String ta = tmp.poll();//tipo de acci�n	
		System.out.print("accion de ");
		switch(SearchClientActions.valueOf(ta.toUpperCase())) {
			case SELECTCLIENT:
				System.out.println("\tselecionar cliente");
				System.out.println("\t---------------------------------");	
				correct = selectClient(tmp);
				break;
			default: 
				System.out.println("Acci�n no contemplada");
				correct = true;	
		}
		return correct;
	}
	
}