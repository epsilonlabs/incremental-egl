package org.eclipse.epsilon.examples.standalone.eol;


import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.emc.plainxml.PlainXmlModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.control.IExecutionListener;
import org.eclipse.epsilon.eol.parse.EolParser;
import org.eclipse.epsilon.eol.types.EolModelElementType;

public class AllOfKindExample {

		
	public static void main(String[] args) throws Exception {
		new AllOfKindExample().run();
	}
	
	public void run() throws Exception {
		EolModule module = new EolModule();
		module.parse("t_a.all.println();");
		
		module.getContext().getExecutorFactory().addExecutionListener(new IExecutionListener() {
			
			Object type = null;
			
			@Override
			public void finishedExecuting(AST ast, Object result, IEolContext context) {
				if (ast.getType() == EolParser.POINT) {	
					if (type instanceof EolModelElementType){
						if (ast.getSecondChild().getText().equals("all") && ast.getSecondChild().getChildren().isEmpty()) {
							System.out.println("All " + ((EolModelElementType) type).getName() + ": " + result);
						}
					}
				}
				
				type = result;
			}
			
			@Override
			public void aboutToExecute(AST ast, IEolContext context) {
				
			}
		});
		
		PlainXmlModel model = new PlainXmlModel();
		model.setName("M");
		model.setXml("<?xml version=\"1.0\"?><a/>");
		model.load();
		
		module.getContext().getModelRepository().addModel(model);
		module.execute();
		
		
		
	}
	
}
