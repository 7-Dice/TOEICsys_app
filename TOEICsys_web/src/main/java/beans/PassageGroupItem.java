package beans;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PassageGroupItem implements Serializable
{
	private long itemId;
	private long groupId;
	private int sequence_no;
	private String speaker;
	private String textBody;
}