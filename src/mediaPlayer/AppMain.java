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
		rootController.setStage(this.primaryStage);	//primaryStage 넘겨주기
		createContextMenu();	//콘텍트메뉴 만들기
		sizeSetting();			//사이즈 설정
		fullScreenAction();		//풀스크린 동작 설정
		//css적용
		scene.getStylesheets().add(getClass().getResource("rootCss.css").toExternalForm());
		this.primaryStage.setScene(scene);
		this.primaryStage.show();
		
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void createContextMenu() {
		rootController.createcontextMenu();	//팝업메뉴 만들기
		rootController.rightMouse();	//우클릭 콘텍트 동작
	}
	
	public void sizeSetting() {
		primaryStage.setWidth(700);	//시작 시 가로
		primaryStage.setMinWidth(550);	//최소 사이즈
		primaryStage.setMinHeight(500);
	}
	
	//전체화면
	public void fullScreenAction() {
		//더블클릭 -> 전체화면
				scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
				        @Override
				        public void handle(MouseEvent doubleClicked) {
				            if(doubleClicked.getClickCount() == 2) {
				                if(primaryStage.isFullScreen()) {
				                	primaryStage.setFullScreen(false);
				                } else {
				                	primaryStage.setFullScreen(true);
				                	rootController.labelPopupAction("전체화면");
				                }
				                
				            }
				            
				        }
				});
				//엔터 -> 전체화면
				scene.setOnKeyPressed(event -> {
					if(event.getCode().equals(KeyCode.ENTER)) {
						 if(primaryStage.isFullScreen()) {
			                	primaryStage.setFullScreen(false);
			                } else {
			                	primaryStage.setFullScreen(true);
			                	rootController.labelPopupAction("전체화면");
			                }
					}
				});
	}
}
