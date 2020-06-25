package mediaPlayer;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PlayList {
//	private Stage stage;
	private Stage playlist;
	private TableView<File> tableView;
	private Button btnUp;
	private Button btnDown;
	private Button btnAdd;
	private Button btnDel;
	private ObservableList<File> fileList;
	
	public TableView<File> getTableView() {
		return tableView;
	}

	public Button getBtnAdd() {
		return btnAdd;
	}

	public Button getBtnDel() {
		return btnDel;
	}
	public Stage getPlayListStage() {
		return playlist;
	}
	
	@SuppressWarnings("unchecked")
	public PlayList(Stage stage) {
		//������ �Ѱ��� ���������� �ٽ� ���������
		if(playlist == null) {
			playlist = new Stage(StageStyle.UTILITY);
			if(!playlist.isShowing() && playlist.getOwner() == null) {
				playlist.initModality(Modality.NONE);
				playlist.initOwner(stage);
				playlist.setTitle("������");
				Parent parent = null;
				try {
					parent = FXMLLoader.load(getClass().getResource("playlist.fxml"));
				} catch (Exception exception) {exception.printStackTrace();}
				//��� �ʱ�ȭ
				tableView = (TableView<File>)parent.lookup("#tableView");
				tableView.setPlaceholder(new Label("������ �����"));
				btnUp = (Button)parent.lookup("#btnUp");
				btnDown = (Button)parent.lookup("#btnDown");
				btnAdd = (Button)parent.lookup("#btnAdd");
				btnDel = (Button)parent.lookup("#btnDel");
					btnUp.setOnAction(e -> btnUpDownAction(e));
					btnDown.setOnAction(e -> btnUpDownAction(e));
//				�����Ͽ��� DelŰ ������ ��
					tableView.setOnKeyPressed(event -> {
						boolean tmp = tableView.getSelectionModel().getSelectedItems().isEmpty();
						if(event.getCode().equals(KeyCode.DELETE) && tmp != true){
							int selectIdx = tableView.getSelectionModel().getSelectedIndex();
							fileList.remove(selectIdx);
						}
					});
				Scene scene = new Scene(parent);
				scene.getStylesheets().add(getClass().getResource("playlistCss.css").toExternalForm());
				playlist.setScene(scene);
				//Ǯ��ũ���� �ƴ϶�� �׻� �÷��̾� �����ʿ� �߰�
				if(!stage.isFullScreen()){
					double x = stage.getX() + stage.getWidth() - 5.0;
					double y = stage.getY();
					playlist.setX(x);
					playlist.setY(y);
				}
				playlist.show();
			}
			
		}
		
		
	}
	
	public void playlistShow() {
		playlist.show();
	}
	//���̺� ���� �ֱ�
	public void tableSetItem(ObservableList<File> fileList) {
		this.fileList = fileList;
		tableView.setItems(fileList);
		//������ �̸�
		TableColumn<File, ?> tcFileName = tableView.getColumns().get(0);
		tcFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
	}
		
		//�������� ��ư up,down (���� �ٲٱ�)
		public void btnUpDownAction(ActionEvent e) {
			int selectIdx = tableView.getSelectionModel().getSelectedIndex();
			//btnUp�̸�
			try {
				if(e.getTarget().toString().indexOf("btnUp") != -1) {
					if(selectIdx > 0) {
						File tmp = fileList.get(selectIdx - 1);
						fileList.remove(selectIdx - 1);
						fileList.add(selectIdx, tmp);
					}else { return;}
				}else {
					if(selectIdx < fileList.size()-1) {
						File tmp = fileList.get(selectIdx + 1);
						fileList.remove(selectIdx + 1);
						fileList.add(selectIdx, tmp);
					}else { return;}
				}
			}catch (Exception e2) {
				return;
			}
			
			
		}

}
