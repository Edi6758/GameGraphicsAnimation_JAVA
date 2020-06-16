import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{ //Canvas é usado para desenhar no Java tendo as propriedades da janela do jogo
	
	//Atributos
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 240; //LARGURA DE JANELA
	private final int HEIGHT = 160; //ALTURA DA JANELA
	private final int SCALE = 3; //ESCALA DE MULTIPLICAÇÃO 
	private int curAnimation = 0, maxAnimation = 3;
	
	private BufferedImage image; //
	
	private Spritesheet sheet; //INSTANCIANDO UMA CLASSE QUE NÓS CRIAMOS PARA POR O PERSSONAGEM
	private BufferedImage[] player; //CRIANDO UM ARRAY DE PLAYER COM TODOS OS SPRITES DO JOGADOR
	private int frames = 0;
	private int maxFrames = 20; //VELOCIDADE DO PERSSONAGEM, QUANTO MENOR MAIS RAPIDO
	
	
	//Métodos
	
	//Método construtor
	public Game() {
		sheet = new Spritesheet("/spritesheet.png"); //ADD O SPRITE DO PERSSONAGEM
		player = new BufferedImage[4]; //TAMANHO DO ARRAY
		player[0] = sheet.getSprite(0, 0, 16, 16); //POSIÇÕES DO ARRAY
		player[1] = sheet.getSprite(16, 0, 16, 16);
		player[2] = sheet.getSprite(32, 0, 16, 16);
		player[3] = sheet.getSprite(48, 0, 16, 16);
		this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE)); //DIMENSÃO DA JANELA
		initFrame();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); 

	}
	
	//FICA ATUALIZANDO O JOGO A 60 FPS COM TODAS AS PROPRIEDADES GRÁFICAS DA JANELA
	private void initFrame() {
		frame = new JFrame("Game do Edi"); //JANELA DO JOGO SENDO INICIALIZADA
		frame.add(this); //ADD O PROPRIO CANVAS
		frame.setResizable(false); // O USER N PODE REDIMENSIONAR A JANELA
		frame.pack(); //CALCULAR DIMENSÕES
		frame.setLocationRelativeTo(null); //JANELA NO CENTRO
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //QUANDO FECHAR ACABAR O PROGRAMA
		frame.setVisible(true); //FICA VISIVEL AO INICIALZAR
	}
	
	//INICIALIZA O JOGO
	public synchronized void start() { 
		thread = new Thread(this); //EXECUTA VARIAS COISAS SIMULTANEAMENTE
		isRunning = true; //ENQUANTO FOR VERDADEIRO O JOGO VAI FICAR EM LOOPING
		thread.start(); //INICIA A THREAD
	}
	
	//PARA O JOGO 
	public synchronized void stop() { 
		isRunning = false; //cancela o looping do run fechando todas as treads
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//MÉTODO PRINCIPAL
	public static void main(String args[]) {
		Game game = new Game(); //INSTANCIA A JANELA
		game.start();
	}
	
	
	//RESETE DA INIMAÇÃO DO PERSSONAGEM
	public void tick() {
		frames++;
		if (frames > maxFrames) {
			frames = 0;
			curAnimation++;
			if(curAnimation > maxAnimation) {
				curAnimation = 0;
			}
		}
	}
	
	//RENDERIZAÇÃO DO JOGO
	public void render() {
		BufferStrategy bs = this.getBufferStrategy(); //SEQUENCIA DE BUFFS NA TELA
		if(bs == null) { //SE N EXISITIR NENHUMA SEQUENCIA DE BUFFS NA TELA CRIA UMA
			this.createBufferStrategy(3);
			return; //QUEBRA O MÉTODO
		}
		
		Graphics g = image.getGraphics(); //RENDERIZAÇÃO DE FORMAS/IMAGENS/TEXTOS ETC
		g.setColor(new Color(0, 0, 250)); //COR DE FUNDO DA TELA RENDERIZADA EM RGB
		g.fillRect(0, 0, WIDTH, HEIGHT); //CRIAÇÃO DO RENTANGULO USANDO OS TAMANHOS ESTIPULADOS ANTERIORMENTE PRA DEFINIR A ALTURA E LARGURA
		
		/*Renderização do Jogo*/
		Graphics2D g2 = (Graphics2D) g;
		
		g.drawImage(player[curAnimation],20,20,null); //ADD O PERSSONAGEM NA TELA EM TAL POSIÇÃO
		/****/
		g.dispose(); //LIMPA DADOS DA IMAGEM NÃO USADOS
		g = bs.getDrawGraphics(); //COLOCA O PERSSONAGEM NA TELA
		g.drawImage(image, 0, 0 , WIDTH*SCALE, HEIGHT*SCALE, null); //DESENHA A IMAGEM NA TELA APOS TER DEFINIDO OQUE IA SER DESENHADO NELA
		bs.show(); //MOSTRA DE FATO OS GRÁFICOS, SEM ISSO N FUNCIONA
	}
	

	/*Pega os nanotimes divide por uma constante calcula o ns
	 * para ver o momento certo que deve ser feito o update do jogo
	 * cria um looping usando o tempo atual menos a ultima vez e divide por ns
	 * se o tempo for maior que um, atualiza e renderiza o jogo continuando assim em looping
	 * */
	
	public void run() { //criado pelo IMPLEMENTS RUNNABLE
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0; //60 FRAMES POR SEGUNDO
		double ns = 1000000000/amountOfTicks; //1 SEGUNDO EM FORMATO DE NADO / 60 SEGUNDOS
		double delta = 0; 
		int frames = 0;
		double timer = System.currentTimeMillis(); 
		
		//LOOPING DO JOGO 
		while(isRunning) {
			long now = System.nanoTime(); //PEGA O TEMPO DO PC EM NANOS SEGUNDOS
			delta+= (now - lastTime)/ns; //QUANDO O DELTA FOR 1 DA O TICK PRA FECHAR 60 TIKS POR SEGUNDO
			lastTime = now;
			if(delta >= 1) {
				tick(); //FICA ATUALIZANDO POR SEGUNDO A TELA
				render(); //FICA ATUALIZANDO POR SEGUNDO A ANIMAÇÃO
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >=1000) { //PASSOU UM SEGUNDO APÓS PASSAR A MSG
				System.out.println("FPS: "+ frames); 
				frames = 0; 
				timer+=1000;
			}
			
		}
		
		stop(); //CASO DE PROBLEMA NO PROGRAMA ELE FORÇA O PROGRAMA A FECHAR
	}
	
}
