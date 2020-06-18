package rad.gui;

import rad.parser.Program;
import rad.parser.ProgramError;
import rad.parser.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Obsługuje wpisywanie formuł
 */
public class InputPanel extends JPanel {
    DefaultListModel<String> historyModel,variablesModel;
    JList<String> history,variables;
    JPanel main;
    JTextField input;
    Program program;
    public InputPanel(Program program)
    {
        super(new BorderLayout());
        this.program = program;
        historyModel = new DefaultListModel<>();
        variablesModel = new DefaultListModel<>();
        history = new JList<String>(historyModel);
        variables = new JList<String>(variablesModel);

        JTextField input = new JTextField(10);
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Value val = new Value();
                try{
                    val = program.eval(input.getText());
                }catch (ProgramError pre)
                {
                    JOptionPane.showMessageDialog(null,
                            "Bledne polecenie:"+ pre.errstr,
                            "Blad programu",
                            JOptionPane.ERROR_MESSAGE);
                }
                catch (NullPointerException nullpo)
                {
                    JOptionPane.showMessageDialog(null,
                            "Null ptr excetion:"+ nullpo.getLocalizedMessage()+"\n Blad ten moze byc spowodowany niezdefiniowana zmienna, lub brakującym nawiasem.",
                            "Blad programu",
                            JOptionPane.ERROR_MESSAGE);
                }
                catch (StackOverflowError so)
                {
                    JOptionPane.showMessageDialog(null,
                            "Blad:"+ so.getLocalizedMessage()+"\n Blad ten moze byc spowodowany blednym uzyciem rekursji",
                            "Blad programu",
                            JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "Nieokreślony blad:"+ ex.getLocalizedMessage(),
                            "Blad programu",
                            JOptionPane.ERROR_MESSAGE);
                }

                historyModel.add(0,input.getText()+" => "+val);
                input.setText("");
            }

        });




        JScrollPane scroll = new JScrollPane(history);
        add(scroll,BorderLayout.WEST);
        //add(variables,BorderLayout.EAST);
        add(input,BorderLayout.PAGE_END);
    }
}
