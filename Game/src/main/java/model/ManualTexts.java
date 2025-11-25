package model;

public final class ManualTexts {
    public static final String FULL_MANUAL = """
          
            
                  MANUAL DEL JUEGO – Oregon Trail Survival
            
                  1. Objetivo del juego
            
                  Eres un viajero en el viejo oeste que debe cruzar tres escenarios peligrosos:
            
                  1. Llanuras de Missouri
                  2. Montañas rocosas
                  3. Río cerca a Oregon
            
                  En cada escenario debes:
            
                  * Sobrevivir a los enemigos.
                  * Administrar tus municiones y suministros.
                  * Encontrar las 4 llaves escondidas en el mapa.
                  * Llegar al portal que aparece cuando tienes las 4 llaves.
            
                  Al superar los tres escenarios, ganas la partida. Durante el juego también puedes desbloquear **logros** que dan puntos.
            
                  ________________________________________________________________________________________________________________________________
            
                  2. Controles
            
                  MOVIMIENTO
            
                  * W – Mover hacia arriba
                  * S – Mover hacia abajo
                  * A – Mover hacia la izquierda
                  * D – Mover hacia la derecha
            
                  APUNTAR Y DISPARAR
            
                  * Mouse – Apunta con la mira.
                  * Click izquierdo – Disparar hacia la mira.
            
                  ARMAS
            
                  * C – Cambiar de arma (entre revólver y rifle, solo si ya recogiste el rifle).
                  * F – Recargar el cargador del arma equipada.
            
                  SUMINISTROS
            
                  * E - Cambiar suministro
                  * R – Usar el suministro seleccionado.
            
                  PANTALLAS ESPECIALES
            
                  * M – Abrir el manual del juego.
                  
                  FUNCIONES ESPECIALES
                  
                  * T - Generar dialogo del jugador (Usando Gemini)
      
                  ________________________________________________________________________________________________________________________________
            
                  3. Indicadores en pantalla (HUD)
            
                  En la parte superior de la pantalla verás:
            
                    VIDAS
            
                    * Se muestran con corazones:
                      
                      - Corazón rojo = vida actual.
                      - Corazón blanco = vida perdida.
                      
                    * El jugador tiene 3 corazones máximo.
            
                    ARMA ACTUAL
            
                    * Se muestra el icono del arma equipada:
            
                      - Revólver.
                      - Rifle (solo aparece cuando lo obtienes).
                      
                    * El arma equipada afecta el daño y el tamaño del cargador.
            
                  MUNICIÓN
            
                    * Munición total: número total de balas que tienes.
                    * Cargador: cuántas balas hay cargadas en el arma actual.
            
                  SUMINISTROS
            
                    * Icono de comida con un número: cantidad de raciones.
                    * Icono de medicina con un número: cantidad de medicinas.
            
                  ________________________________________________________________________________________________________________________________
            
                  4. Armas y municiones
            
                  El juego tiene dos armas:
            
                  REVOLVER
            
                  * Es el arma inicial.
                  * Cargador máximo: 9 balas.
                  * Daño normal por disparo.
                  * Se puede usar mientras tengas balas totales disponibles.
            
                  RIFLE
            
                  * Aparece como un arma tirada en el suelo.
                  * Debes caminar encima del rifle para recogerlo.
                  * Una vez recogido, puedes alternar entre revólver y rifle con la tecla C.
                  * Cargador máximo: 5 balas.
                  * Hace más daño que el revólver por disparo.
            
                  SISTEMA DE MUNICIONES
            
                  * Ambas armas usan el mismo pool de munición total.
                  * Cada vez que disparas, se gasta 1 bala del cargador y 1 bala del total.
                  * Cuando recargas (F):
            
                    - Se rellenan las balas del cargador del arma actual, hasta su máximo.
                    - Las balas salen de la munición total.
                    - Si no tienes balas suficientes, se rellena solo lo que quede.
                    
                  * Si intentas disparar sin balas en el cargador, el arma no dispara.
            
                  ________________________________________________________________________________________________________________________________
            
                  5. Suministros: comida y medicinas
            
                  En el mapa hay ítems que reaparecen cada cierto tiempo:
            
                  COMIDA
            
                    * Cura 1 corazón.
                    * Si ya estás con vida máxima, no se consume.
            
                  MEDICINA
            
                    * Cura 2 corazones (hasta el máximo de 3).
                    * Si ya estás con vida máxima, tampoco debería consumirse.
            
                  CÓMO FUNCIONA:
            
                  1. Camina sobre el ítem (comida o medicina) para recogerlo.
            
                     * Al recogerlo:
            
                       - El ítem desaparece.
                       - Se suma al inventario del jugador.
                       - Tras algunos segundos, el ítem reaparece en el mismo lugar.
            
                  2. Para usarlos:
            
                     * Intercambia entre suministros con la letra E.
                     * Pulsa R para usar el suministro seleccionado.
                     * El HUD resalta cuál está seleccionado (Se marca en negrilla la cantidad del suministro seleccionado).
            
                  ________________________________________________________________________________________________________________________________
            
                  6. Enemigos
            
                  Hay varios enemigos repartidos por el mapa:
            
                  * Se mueven hacia el jugador.
                  * Algunos disparan balas.
                  * Otros pueden hacer daño por contacto.
            
                  DAÑO AL JUGADOR
            
                  * Cada golpe (bala o contacto) quita corazones de vida.
                  * Si la vida llega a 0, se muestra la pantalla de Game Over.
            
                  DAÑO A LOS ENEMIGOS
            
                  * Los enemigos mueren al recibir suficientes disparos.
                  * Algunos son más resistentes que otros (necesitan más balas).
                  * Cuando mueren, pueden dejar un pickup de munición.
            
                  ________________________________________________________________________________________________________________________________
            
                  7. Escenarios y colisiones
            
                  En cada escenario el mapa está dividido en casillas:
            
                  * Zona caminable: el personaje puede moverse libremente.
                  * Obstáculos (árboles, rocas, bordes del río, etc.):
            
                    - El jugador y los enemigos no pueden pasar.
                    
                  * Acantilados / zonas de caída (especialmente en Montañas):
            
                    - Si el jugador pisa una zona de caída:
            
                      # Pierde vida.
                      # Puede reaparecer en un punto seguro del mapa.
                      
                    - Puedes usar este tipo de casilla también para que los enemigos caigan y mueran.
            
                  ________________________________________________________________________________________________________________________________
            
                  8. Llaves y portales
            
                  En cada escenario hay 4 llaves escondidas:
            
                  1. Cada llave aparece en una posición fija del mapa.
                  2. Para recoger una llave, basta con caminar sobre ella.
                  3. Las llaves no reaparecen (son únicas).
            
                  Cuando el jugador recoge las 4 llaves de un escenario:
            
                  * Se activa un portal en una posición fija.
                  * El portal aparece dibujado sobre el mapa.
                  * Si el jugador entra en el área del portal:
            
                    - En el escenario 1 y 2: pasa al siguiente escenario.
                    - En el escenario 3: se muestra la pantalla de victoria.
            
                  ________________________________________________________________________________________________________________________________
            
                  9. Árbol de logros
            
                  El juego incluye un sistema de logros organizado en forma de árbol binario de logros.
                  Algunos ejemplos de logros:
            
                  * Bienvenida al juego.
                  * Primera recarga de munición.
                  * Derrotar al primer enemigo.
                  * Eliminar 5, 15 o 30 enemigos.
                  * Recolectar todas las llaves de cada escenario.
                  * Terminar un escenario con muchas balas ahorradas.
                  * Completar un escenario sin recibir daño.
                  * Terminar la aventura completa.
            
                  Puedes ver el árbol de logros:
            
                  * Desde el menú principal, en el botón “Logros”.
                  * Durante la partida, con el botón "logros" que se ve en el panel.
            
                  En la pantalla de victoria se muestra el puntaje total obtenido con los logros desbloqueados.
            
                  ________________________________________________________________________________________________________________________________
            
                  10. Pantallas del juego
            
                  * Menú principal
            
                    * Jugar / empezar partida.
                    * Árbol de logros.
                    * Manual del juego.
                    * Salir.
            
                  * Pantallas de escenario
            
                    * Mapa + HUD con vida, arma, munición, suministros, etc.
            
                  * Pantalla de Game Over
            
                    - Aparece al morir.
                    - Permite:
            
                      # Volver al menú.
                      # Empezar una nueva partida.
            
                  * Pantalla de Victoria
            
                    - Aparece al superar el último escenario y entrar al portal final.
                    - Muestra el puntaje total de logros.
                    - Permite:
            
                      # Jugar de nuevo.
                      # Volver al menú.
            
                  * Pantalla del manual
            
                    - Muestra este manual.
                    - Se puede abrir:
            
                      # Desde el menú principal (botón “Manual”).
                      # Durante la partida con la tecla M.
            
    """;
    private ManualTexts() {}
}

