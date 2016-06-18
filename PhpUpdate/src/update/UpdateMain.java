package update;

public class UpdateMain {

	public static void main(String[] args) {
		String rootDir = null;
		if (args.length <= 0){
			System.out.println("Please input phpDirectory!");
		}else {
			for (int i = 0; i < args.length; i++){
				rootDir = args[i];
				rootDir = rootDir.replace("\\", "\\\\");
				PhpUpdate update = new PhpUpdate(rootDir);
				update.traverseRoot();
			}
			System.out.println("Total changes:"+PhpUpdate.getTotalChange());
		}
	}
}
