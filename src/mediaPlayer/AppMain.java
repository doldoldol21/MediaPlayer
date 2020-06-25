package mediaPlayer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AppMain extends Application{
	Stage primaryStage;
	Scene scene;
	RootController rootController;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("root.fxml"));
		Parent root = loader.load();
		rootController = loader.getController();
		scene = new Scene(root);
		rootController.setStage(this.primaryStage);	//primaryStage �Ѱ��ֱ�
		createContextMenu();	//����Ʈ�޴� �����
		sizeSetting();			//������ ����
		fullScreenAction();		//Ǯ��ũ�� ���� ����
		//css����
		scene.getStylesheets().add(getClass().getResource("rootCss.css").toExternalForm());
		this.primaryStage.setScene(scene);
		this.primaryStage.show();
		
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void createContextMenu() {
		rootController.createcontextMenu();	//�˾��޴� �����
		rootController.rightMouse();	//��Ŭ�� ����Ʈ ����
	}
	
	public void sizeSetting() {
		primaryStage.setWidth(700);	//���� �� ����
		primaryStage.setMinWidth(550);	//�ּ� ������
		primaryStage.setMinHeight(500);
	}
	
	//��üȭ��
	public void fullScreenAction() {
		//����Ŭ�� -> ��üȭ��
				scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
				        @Override
				        public void handle(MouseEvent doubleClicked) {
				            if(doubleClicked.getClickCount() == 2) {
				                if(primaryStage.isFullScreen()) {
				                	primaryStage.setFullScreen(false);
				                } else {
				                	primaryStage.setFullScreen(true);
				                	rootController.labelPopupAction("��üȭ��");
				                }
				                
				            }
				            
				        }
				});
				//���� -> ��üȭ��
				scene.setOnKeyPressed(event -> {
					if(event.getCode().equals(KeyCode.ENTER)) {
						 if(primaryStage.isFullScreen()) {
			                	primaryStage.setFullScreen(false);
			                } else {
			                	primaryStage.setFullScreen(true);
			                	rootController.labelPopupAction("��üȭ��");
			                }
					}
				});
	}
}
